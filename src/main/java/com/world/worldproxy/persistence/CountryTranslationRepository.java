package com.world.worldproxy.persistence;

import com.world.worldproxy.entity.CountryTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CountryTranslationRepository extends JpaRepository<CountryTranslation, Integer> {

    @Override
    List<CountryTranslation> findAll();

    List<CountryTranslation> findByTranslation(String translation);
}
