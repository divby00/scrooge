package org.wildcat.scrooge.persistence.beans;


import java.io.Serializable;


public class TbGasto implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 5437289216217641636L;
	private Integer				idGasto;
	private Integer				idCategoria;
	private Double				importe;
	private Long				fecha;
	private String				nombreCategoria;


	public Integer getIdGasto() {
		return idGasto;
	}


	public void setIdGasto(Integer idGasto) {
		this.idGasto = idGasto;
	}


	public Integer getIdCategoria() {
		return idCategoria;
	}


	public void setIdCategoria(Integer idCategoria) {
		this.idCategoria = idCategoria;
	}


	public Double getImporte() {
		return importe;
	}


	public void setImporte(Double importe) {
		this.importe = importe;
	}


	public Long getFecha() {
		return fecha;
	}


	public void setFecha(Long fecha) {
		this.fecha = fecha;
	}


	public String getNombreCategoria() {
		return nombreCategoria;
	}


	public void setNombreCategoria(String nombreCategoria) {
		this.nombreCategoria = nombreCategoria;
	}
}
