package com.sintropia.calculator.mapper;

public interface Mapper<E, T> {
	T toDTO(E entity);
}
