var idCompra = $("#idCompra").val();
var compraDetalle = "";
$("#cuerpoDevolucion").load("/devolucionesCompras/cuerpoDevolucion/"+idCompra);

$("#devolverArticuloConSerial").click(function(e){
	e.preventDefault();
	var cantidad = $("#cantArticuloSerialADevolver").val();
	if(cantidad == 0){
		var seleccion = $("#seleccionSeriales").val();
		var seriales = String(seleccion);
		 $.post("/devolucionesCompras/ajax/devolverSeriales/", {
			 'compraDetalle':compraDetalle,
			 'seriales': ''
	   },function(response){
		   $("#articuloSerialModal").modal("hide");
		   $("#cuerpoDevolucion").load("/devolucionesCompras/cuerpoDevolucion/"+idCompra);
	 	});
	}else{
		var seleccion = $("#seleccionSeriales").val();
		var seriales = String(seleccion);
		 $.post("/devolucionesCompras/ajax/devolverSeriales/", {
			 'compraDetalle':compraDetalle,
			 'seriales': seriales
	   },function(response){
		   $("#articuloSerialModal").modal("hide");
		   $("#cuerpoDevolucion").load("/devolucionesCompras/cuerpoDevolucion/"+idCompra);
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
		 $.post("/devolucionesCompras/ajax/devolverSinSeriales/", {
			 'compraDetalle':compraDetalle,
			 'cantidad': cantidad
	   },function(response){
		   $("#articuloModal").modal("hide");
		   $("#cuerpoDevolucion").load("/devolucionesCompras/cuerpoDevolucion/"+idCompra);
	 	});
	}
});

//$("#btnGuardar").click(function(e){
//	e.preventDefault();
//	var idFactura = $("#idFactura").val();
//	 $.post("/devolucionesFacturas/ajax/crearDevolucion/", {
//		 'idFactura':idFactura
//	   },function(response){
//		   Swal.fire({
//				title : 'Muy bien!',
//				text : 'Se ha creado la devolucion',
//				position : 'top',
//				icon : 'success',
//				confirmButtonText : 'Cool'
//			})
//		   setTimeout(function() {
//				location.href = '/devolucionesFacturas/';
//			}, 2000);
//	   });
//});
//
function seleccionarSeriales(){
	var cantidad = 0;
	var seriales = "";
	var seleccion = $("#seleccionSeriales").val();
	if(seleccion.length>0){
		seriales = String(seleccion).split(",");
		cantidad = seriales.length;
		$('#costoArticuloConSerial').load("/devolucionesCompras/ajax/obtenerCostos/"+compraDetalle+"/"+seriales,function(){
			var costoTotal = $('#totalCostoSerial').val();
			$('#costoTotalSerial').val(costoTotal);
		});
	}else{
		$('#costoTotalSerial').val(0);
	}
	$("#cantArticuloSerialADevolver").val(cantidad);
}
	
function modalSerialesDetalleCompra(IdcompraDetalle){
	compraDetalle = IdcompraDetalle;
	$('#serialesAcct').load("/devolucionesCompras/ajax/obtenerSeriales/"+IdcompraDetalle,function(){
		$("#serialesAcctModal").modal("show");
	});
}

function devolverArticulo(id) {
	compraDetalle = id;
	//verificamos si el articulo es con serial o sin serial.
	$("#tipoArticulo").load("/devolucionesCompras/verificarTipoArticulo/"+id,function(){
		if($("#conSerial").val() == 0){
			//No tiene serial
			var cantidadDevuelta = $("#cantidadDevuelta").val();
			var cantidadArticulo = $("#cantidadArticulo").val();
			var disponible = parseFloat(cantidadArticulo)-parseFloat(cantidadDevuelta);
			$("#nombreArticuloSinSerial").val($("#nombreArticulo").val());
			$("#costoArticuloSinSerial").val($("#costoArticulo").val());	
			$("#cantidadTotalArticuloSinSerial").val(disponible);	
			$('#cantidadArticuloSinSerial').prop('max', disponible);
			$("#articuloModal").modal("show");
		}else{
			//articulo con serial
			$('#serialesADevolver').load("/devolucionesCompras/ajax/infoSerialesADevolver/"+id,function(){
				$("#articuloSerialModal").modal("show");
			});
			
		}
	});
}