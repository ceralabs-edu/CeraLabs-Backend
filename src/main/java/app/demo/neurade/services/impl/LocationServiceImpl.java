package app.demo.neurade.services.impl;

import app.demo.neurade.domain.models.Commune;
import app.demo.neurade.domain.models.Province;
import app.demo.neurade.infrastructures.repositories.CommuneRepository;
import app.demo.neurade.infrastructures.repositories.ProvinceRepository;
import app.demo.neurade.services.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final ProvinceRepository provinceRepository;
    private final CommuneRepository communeRepository;

    @Override
    public List<Province> getProvinces() {
        return provinceRepository.findAll();
    }

    @Override
    public List<Commune> getCommunes(Integer provinceId) {
        return communeRepository.findByProvinceId(provinceId);
    }
}
