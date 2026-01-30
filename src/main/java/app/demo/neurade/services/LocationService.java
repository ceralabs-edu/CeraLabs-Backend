package app.demo.neurade.services;

import app.demo.neurade.domain.models.Commune;
import app.demo.neurade.domain.models.Province;

import java.util.List;

public interface LocationService {
    List<Province> getProvinces();
    List<Commune> getCommunes(Integer provinceId);
}
