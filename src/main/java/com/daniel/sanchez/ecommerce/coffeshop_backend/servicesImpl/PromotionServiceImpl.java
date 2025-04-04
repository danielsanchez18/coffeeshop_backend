package com.daniel.sanchez.ecommerce.coffeshop_backend.servicesImpl;

import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.PromotionDTO;
import com.daniel.sanchez.ecommerce.coffeshop_backend.dto.PromotionProductDTO;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Product;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.Promotion;
import com.daniel.sanchez.ecommerce.coffeshop_backend.entities.PromotionProduct;
import com.daniel.sanchez.ecommerce.coffeshop_backend.mappers.PromotionMapper;
import com.daniel.sanchez.ecommerce.coffeshop_backend.repositories.ProductRepository;
import com.daniel.sanchez.ecommerce.coffeshop_backend.repositories.PromotionProductRepository;
import com.daniel.sanchez.ecommerce.coffeshop_backend.repositories.PromotionRepository;
import com.daniel.sanchez.ecommerce.coffeshop_backend.services.PromotionService;
import com.daniel.sanchez.ecommerce.coffeshop_backend.utils.CalculatePricePromotionUtil;
import com.daniel.sanchez.ecommerce.coffeshop_backend.validators.PromotionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PromotionServiceImpl implements PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private PromotionProductRepository promotionProductRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PromotionMapper promotionMapper;

    @Autowired
    private PromotionValidator promotionValidator;

    @Override
    @Transactional
    public PromotionDTO create(PromotionDTO promotionDTO) {
        promotionValidator.validateCreatePromotion(promotionDTO);

        Promotion promotion = promotionMapper.toEntity(promotionDTO);
        promotion.setPriceFinal(calculateFinalPrice(promotionDTO));

        Promotion savedPromotion = promotionRepository.save(promotion);
        savePromotionProducts(promotionDTO.getProducts(), savedPromotion);

        return promotionMapper.toDTO(savedPromotion);
    }

    @Override
    @Transactional
    public PromotionDTO update(UUID id, PromotionDTO promotionDTO) {
        Promotion existing = getPromotionOrThrow(id);

        promotionValidator.validateUpdatePromotion(id, promotionDTO);
        promotionMapper.updateEntityFromDTO(promotionDTO, existing);

        // Recalcula el precio final si hay cambios relevantes
        if (needRecalculation(promotionDTO)) {
            existing.setPriceFinal(calculateFinalPrice(promotionDTO));
        }

        return promotionMapper.toDTO(promotionRepository.save(existing));
    }

    @Override
    public List<PromotionDTO> findAll() {
        return promotionRepository.findAll().stream()
                .map(promotionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<PromotionDTO> findAll(Pageable pageable) {
        return promotionRepository.findAll(pageable)
                .map(promotionMapper::toDTO);
    }

    @Override
    @Transactional
    public PromotionDTO changeState(UUID id) {
        Promotion promotion = getPromotionOrThrow(id);

        promotionValidator.validateCanEndPromotion(promotion);
        promotion.cancel();

        return promotionMapper.toDTO(promotionRepository.save(promotion));
    }

    @Override
    @Transactional
    public PromotionDTO extendPromotion(UUID id, PromotionDTO promotionDTO) {
        Promotion existingPromotion = getPromotionOrThrow(id);

        promotionValidator.validateExtendPromotion(existingPromotion, promotionDTO);

        // Aplicar cambios según estado
        String status = existingPromotion.calculateStatus();
        existingPromotion.setEndDate(LocalDateTime.parse(promotionDTO.getEndDate()));

        if (status.equals("AGOTADA")) {
            existingPromotion.setUsesMax(promotionDTO.getUsesMax());
        }

        return promotionMapper.toDTO(promotionRepository.save(existingPromotion));
    }

    @Override
    @Transactional
    public PromotionDTO incrementUsage(UUID id) {
        Promotion promotion = getPromotionOrThrow(id);

        promotionValidator.validateCanIncrementUsage(promotion);
        promotion.incrementUsage();

        return promotionMapper.toDTO(promotionRepository.save(promotion));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Promotion promotion = getPromotionOrThrow(id);
        promotionProductRepository.deleteByPromotionId(id);
        promotionRepository.delete(promotion);
    }

    @Override
    public PromotionDTO findById(UUID id) {
        Promotion promotion = getPromotionOrThrow(id);
        return promotionMapper.toDTO(promotion);
    }

    @Override
    public Page<PromotionDTO> findByName(String name, Pageable pageable) {
        return promotionRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(promotionMapper::toDTO);
    }

    @Override
    public Page<PromotionDTO> findByProductName(String productName, Pageable pageable) {
        return promotionProductRepository.findByProductName(productName, pageable)
                .map(pp -> {
                    Promotion promotion = pp.getPromotion();
                    return promotionMapper.toDTO(promotion);
                });
    }

    @Override
    public List<PromotionDTO> findByState(boolean active) {
        LocalDateTime now = LocalDateTime.now();
        return active ?
                promotionRepository.findActivePromotions(now).stream()
                        .map(promotionMapper::toDTO)
                        .collect(Collectors.toList()) :
                promotionRepository.findInactivePromotions(now).stream()
                        .map(promotionMapper::toDTO)
                        .collect(Collectors.toList());
    }

    @Override
    public Page<PromotionDTO> findByStatePageable(boolean active, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        return active ?
                promotionRepository.findActivePromotions(now, pageable)
                        .map(promotionMapper::toDTO) :
                promotionRepository.findInactivePromotions(now, pageable)
                        .map(promotionMapper::toDTO);
    }

    // --- Métodos privados de ayuda ---

    private Promotion getPromotionOrThrow(UUID id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Promoción no encontrada"));
    }

    private Double calculateFinalPrice(PromotionDTO promotionDTO) {
        double originalPrice = CalculatePricePromotionUtil.calculateOriginalPrice(promotionDTO.getProducts(), productRepository);
        return CalculatePricePromotionUtil.calculateFinalPrice(promotionMapper.toEntity(promotionDTO), originalPrice);
    }

    private void savePromotionProducts(List<PromotionProductDTO> products, Promotion promotion) {
        products.forEach(pp -> {
            PromotionProduct promotionProduct = new PromotionProduct();
            promotionProduct.setPromotion(promotion);
            Product product = productRepository.findById(pp.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
            promotionProduct.setProduct(product);
            promotionProduct.setQuantityRequired(pp.getQuantityRequired());
            promotionProductRepository.save(promotionProduct);
        });
    }

    private boolean needRecalculation(PromotionDTO dto) {
        return dto.getDiscountType() != null ||
                dto.getDiscountValue() != null ||
                dto.getProducts() != null;
    }
}