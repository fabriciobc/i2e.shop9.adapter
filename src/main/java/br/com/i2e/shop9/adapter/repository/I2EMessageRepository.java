package br.com.i2e.shop9.adapter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.i2e.common.model.I2EMessage;

public interface I2EMessageRepository extends JpaRepository<I2EMessage, Long> {

}
