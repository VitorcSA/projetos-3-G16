package com.sintropia.calculator.mapper;

public interface Mapper<E, D> {
	D toDTO(E entity);
}
