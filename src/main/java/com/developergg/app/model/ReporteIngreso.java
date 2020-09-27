package com.developergg.app.model;

public class ReporteIngreso {
	
	private Integer id;
	private String detalle;
	private String fecha;
	private String usuario;
	private Double monto;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getDetalle() {
		return detalle;
	}
	public void setDetalle(String detalle) {
		this.detalle = detalle;
	}
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public Double getMonto() {
		return monto;
	}
	public void setMonto(Double monto) {
		this.monto = monto;
	}
	@Override
	public String toString() {
		return "ReporteIngreso [id=" + id + ", detalle=" + detalle + ", fecha=" + fecha + ", usuario=" + usuario
				+ ", monto=" + monto + "]";
	}
}
