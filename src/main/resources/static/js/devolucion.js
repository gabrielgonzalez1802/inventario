var idFactura = $("#idFactura").val();
var facturaDetalle = "";
$("#cuerpoDevolucion").load("/devolucionesFacturas/cuerpoDevolucion/"+idFactura);

$("#devolverArticuloConSerial").click(function(e){
	e.preventDefault();
	var cantidad = $("#cantArticuloSerialADevolver").val();
	if(cantidad == 0){
		var seleccion = $("#seleccionSeriales").val();
		var seriales = String(seleccion);
		 $.post("/devolucionesFacturas/ajax/devolverSeriales/", {
			 'facturaDetalle':facturaDetalle,
			 'seriales': ''
	   },function(response){
		   $("#articuloSerialModal").modal("hide");
		   $("#cuerpoDevolucion").load("/devolucionesFacturas/cuerpoDevolucion/"+idFactura);
	 	});
	}else{
		var seleccion = $("#seleccionSeriales").val();
		var seriales = String(seleccion);
		 $.post("/devolucionesFacturas/ajax/devolverSeriales/", {
			 'facturaDetalle':facturaDetalle,
			 'seriales': seriales
	   },function(response){
		   $("#articuloSerialModal").modal("hide");
		   $("#cuerpoDevolucion").load("/devolucionesFacturas/cuerpoDevolucion/"+idFactura);
	 	});
	}
});

$("#devolverArticuloSinSerial").click(function(e){
	var cantidad = $("#cantidadArticuloSinSerial").val();
	var cantidadTtotal = $("#cantidadTotalArticuloSinSerial").val();
	if(parseFloat(cantidad)>parseFloat(cantidadTtotal)){
		Swal.fire({
			title : 'Advertencia!',
			text : 'La cantidad a devolver no puede ser mayor a la cantidad total',
			position : 'top',
			icon : 'warning',
			confirmButtonText : 'Cool'
		})
	}else{
		 $.post("/devolucionesFacturas/ajax/devolverSinSeriales/", {
			 'facturaDetalle':facturaDetalle,
			 'cantidad': cantidad
	   },function(response){
		   $("#articuloModal").modal("hide");
		   $("#cuerpoDevolucion").load("/devolucionesFacturas/cuerpoDevolucion/"+idFactura);
	 	});
	}
});

$("#devolverArticuloTaller").click(function(e){
	var cantidad = $("#cantidadTotalTaller").val();
	var cantidadDevolver = $('#cantidadDevolverTaller').val();
	if(parseFloat(cantidadDevolver) > parseFloat(cantidad)){
		Swal.fire({
			title : 'Advertencia!',
			text : 'La cantidad a devolver no puede ser mayor a la cantidad total',
			position : 'top',
			icon : 'warning',
			confirmButtonText : 'Cool'
		})
	}else{
		 $.post("/devolucionesFacturas/ajax/devolverArticuloTaller/", {
			 'facturaDetalle':facturaDetalle,
			 'cantidad': cantidadDevolver
	   },function(response){
		   $("#articuloTallerModal").modal("hide");
		   $("#cuerpoDevolucion").load("/devolucionesFacturas/cuerpoDevolucion/"+idFactura);
	 	});
	}
})

$("#btnGuardar").click(function(e){
	alert("en desarrollo");
//	e.preventDefault();
//	var idFactura = $("#idFactura").val();
//	 $.post("/devolucionesFacturas/ajax/crearDevolucion/", {
//		 'idFactura':idFactura
//	   },function(response){
////		   $("#articuloTallerModal").modal("hide");
////		   $("#cuerpoDevolucion").load("/devolucionesFacturas/cuerpoDevolucion/"+idFactura);
//	   });
});

function seleccionarSeriales(){
	var cantidad = 0;
	var seriales = "";
	var seleccion = $("#seleccionSeriales").val();
	if(seleccion.length>0){
		seriales = String(seleccion).split(",");
		cantidad = seriales.length;
		$('#precioArticuloConSerial').load("/devolucionesFacturas/ajax/obtenerPrecios/"+facturaDetalle+"/"+seriales,function(){
			var precioTotal = $('#totalPrecioSerial').val();
			var precioTotalConItbis = $('#totalPrecioSerialItbis').val();
			$('#precioTotalSerial').val(precioTotal);
			$('#totalSerialConItbis').val(precioTotalConItbis);
		});
	}else{
		$('#precioTotalSerial').val(0);
		$('#totalSerialConItbis').val(0);
	}
	$("#cantArticuloSerialADevolver").val(cantidad);
}
	
function modalSerialesDetalleFactura(IdfacturaDetalle){
	facturaDetalle = IdfacturaDetalle;
	$('#serialesAcct').load("/devolucionesFacturas/ajax/obtenerSeriales/"+IdfacturaDetalle,function(){
		$("#serialesAcctModal").modal("show");
	});
}

function devolverArticulo(id) {
	facturaDetalle = id;
	//verificamos si el articulo es con serial o sin serial.
	$("#tipoArticulo").load("/devolucionesFacturas/verificarTipoArticulo/"+id,function(){
		if($("#conSerial").val() == 0){
			//No tiene serial
			$("#nombreArticuloSinSerial").val($("#nombreArticulo").val());
			$("#precioArticuloSinSerial").val($("#precioArticulo").val());	
			$("#cantidadTotalArticuloSinSerial").val($("#cantidadArticulo").val());	
			$('#cantidadArticuloSinSerial').prop('max', $("#cantidadArticulo").val());
			$("#articuloModal").modal("show");
		}else{
			//articulo con serial
			$('#serialesADevolver').load("/devolucionesFacturas/ajax/infoSerialesADevolver/"+id,function(){
				$("#articuloSerialModal").modal("show");
			});
			
		}
	});
}

function devolverArticuloTaller(id){
	facturaDetalle = id;
	$('#infoTaller').load("/devolucionesFacturas/ajax/infoArticuloTaller/"+id,function(){
		$("#nombreArticuloTaller").val($("#articuloTaller").val());
		$("#precioArticuloTaller").val($("#precioTaller").val());
		$("#cantidadTotalTaller").val($("#cantidadTaller").val());
		$('#cantidadDevolverTaller').prop('max', $("#cantidadTotalTaller").val());
		$("#articuloTallerModal").modal("show");
	});
}