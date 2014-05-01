package org.wildcat.scrooge.persistence.beans;


import java.io.Serializable;


public class TbCategoria implements Serializable {

	private static final long	serialVersionUID	= -5566517877859093710L;
	private long				id;
	private String				nombre;


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
}
