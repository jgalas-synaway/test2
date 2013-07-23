package com.synaway.oneplaces.repository;

import java.math.BigInteger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

public class SpotRepositoryImpl implements SpotRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Long count(Double latitude, Double longitude, Integer radius, int secondsFrom, int secondsTo) {
        String point = "POINT(" + longitude + " " + latitude + ")";
        Query query = entityManager
                .createNativeQuery("SELECT Count(*) FROM spot WHERE created_at <= now() - INTERVAL '"
                        + secondsTo
                        + " seconds' AND created_at >= now() - INTERVAL '"
                        + secondsFrom
                        + " seconds' AND status <> 'occupied' AND ST_Distance_Sphere(location, ST_SetSRID(ST_GeometryFromText(?1), 4326)) < ?2");
        query.setParameter(1, point);
        query.setParameter(2, radius);
        return ((BigInteger) query.getSingleResult()).longValue();
    }
}
