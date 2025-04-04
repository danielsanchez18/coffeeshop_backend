package com.daniel.sanchez.ecommerce.coffeshop_backend.enums;


public enum OfferState {

    PROXIMA,    // Aún no en vigencia (starDate en futuro)
    ACTIVA,     // En rango de fechas y usos disponibles
    CANCELADA,  // Finalizada manualmente
    AGOTADA,    // Usos alcanzaron el límite (usesQuantity = usesMax)
    TERMINADA;   // Fecha de fin pasó y no se agotó

}
