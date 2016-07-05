package com.meli.api.galaxy.service;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.meli.api.galaxy.model.Galaxy;
import com.meli.api.galaxy.model.Planet;
import com.meli.api.galaxy.model.StatusDay;
import com.meli.api.galaxy.model.Weather;
import com.meli.api.galaxy.repository.GalaxyRepository;
import com.meli.api.galaxy.utils.PlanetUtils;

@Service
public class GalaxyService {

	private static final Logger LOG = LoggerFactory.getLogger(GalaxyService.class);

	private GalaxyRepository repository;
	
	@Autowired
	public GalaxyService(GalaxyRepository repository) {
		this.repository = repository;
	}
	
	public boolean existsSomeGalaxy() {
		return this.repository.count() != 0;
	}
	
	public Galaxy save(Galaxy galaxy) {
		Preconditions.checkArgument(Objects.nonNull(galaxy));
		return this.repository.save(galaxy);
	}

	public Galaxy getByDay(Integer day) {
		List<Galaxy> resultSet = this.repository.findAll();
		Galaxy galaxy = resultSet.get(0);
		
		this.predict(galaxy, day);
		
		LOG.info(galaxy.toString());
		return galaxy;
	}
	
	public void predict(Galaxy galaxy, Integer day) {
		this.movePlanets(galaxy, day);
		this.updateWeather(galaxy, day);
	}

	private void movePlanets(Galaxy galaxy, Integer day) {
		galaxy.movePlanets(day);
	}

	private void updateWeather(Galaxy galaxy, Integer day) {
		Planet vulcano = galaxy.getPlanets().get("vulcano");
		Planet ferengi = galaxy.getPlanets().get("ferengi");
		Planet betasoide = galaxy.getPlanets().get("betasoide");
		
		if (this.isDroughtWeather(vulcano, ferengi, betasoide)) {
			galaxy.setDroughtDays(galaxy.getDroughtDays() + 1);
			galaxy.getDays().put(day, new StatusDay.Builder().day(day).weather(Weather.drought).build());
		}
//		} else if(this.isRainyWeather(vulcano, ferengi, betasoide)) {
//			
//		} else if(this.isOptimalWeather(vulcano, ferengi, betasoide)) {
//			
//		}
	}
	
	private boolean isDroughtWeather(Planet vulcano, Planet ferengi, Planet betasoide) {
		Integer vangle = vulcano.getAngle();
		Integer fangle = ferengi.getAngle();
		Integer bangle = betasoide.getAngle();
		
		return PlanetUtils.areAligned(vangle, fangle) && PlanetUtils.areAligned(fangle, bangle);
	}
	
	private boolean isRainyWeather(Planet vulcano, Planet ferengi, Planet betasoide) {
		return false;
	}
	
	private boolean isOptimalWeather(Planet vulcano, Planet ferengi, Planet betasoide) {
		return false;
	}
}