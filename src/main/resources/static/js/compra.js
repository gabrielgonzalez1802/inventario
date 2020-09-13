var seleccionado = $("#articulos").select2();

//al principio tomara el foco
focusSelectArticuloNew();

$("#no_factura").change(function(){
	//Si no tiene id creamos la compra
	var idCompra = $("#idCompra").val();
	
});

//Cargamos el cuerpo de la compra
cargarDetalles();

//Evento cuando se seleccine un articulo
$('#articulos').on("select2:select", function(e) {
	if(seleccionado.val()!=""){
		var idArticulo = seleccionado.val();
		var valor = $('#articulos :selected').text();
		if(valor!=""){
			$('#infoArticulo').load("/compras/ajax/getInfoArticulo/"+idArticulo,function(data){
				if($("#infoSerial").val()==1){
					$('#infoArticuloSerial').load("/compras/ajax/getInfoArticuloSerial/"+idArticulo,function(data){
						//Con serial
						$("#serialModal").modal("show");
					});
				}else{
					//Sin serial
					$('#infoArticuloSinSerial').load("/compras/ajax/getInfoArticuloSinSerial/"+idArticulo,function(data){
						//Con serial
						$("#sinSerialModal").modal("show");
					});
				}
			});
		}
	}
});

$("#btnAddSinSerial").click(function(e){
	 var idCompra = $("#idCompra").val();
	 var idArticulo = $("#idArticuloSinSerial").val();
	 var cantidad =  $("#cantidadArticuloSinSerial").val();
	 var costo = $("#costoSinSerial").val();
	 var precioMaximo = $("#sinSerialPrecioMaximo").val();
	 var precioMinimo = $("#sinSerialPrecioMinimo").val();
	 var precioMayor = $("#sinSerialPrecioMayor").val();
	 $.post("/compras/ajax/addItemSinSerial/", {
		 'idCompra': idCompra,
		 'idArticulo': idArticulo,
		 'cantidad': cantidad,
		 'costo': costo,
		 'precioMaximo': precioMaximo,
		 'precioMinimo': precioMinimo,
		 'precioMayor': precioMayor
   },function(response){
		$('#responseAddItem').replaceWith(response);
		if($("#responseAddItem").val() == 0){
			Swal.fire({
				title : 'Advertencia!',
				text : 'El articulo no pudo ser agregado a la compra',
				position : 'top',
				icon : 'warning',
				confirmButtonText : 'Cool'
			})
		}else{
			focusSelectArticuloNew();
			cargarDetalles();
			$("#cantidadArticuloSinSerial").val("1");
		}
 	});
});

$("#btnAddSerial").click(function(e){
	e.preventDefault();
	var serial = $("#serial").val();
	var idArticulo = $("#infoIdArticulo").val();
	var costo = parseFloat($("#costoSerial").val());
	var precioMaximo = parseFloat($("#precioMaximo").val());
	var precioMinimo = parseFloat($("#precioMinimo").val());
	var precioMayor = parseFloat($("#precioMayor").val());
	var idCompra = $("#idCompra").val();
	if(!serial){
		Swal.fire({
			title : 'Advertencia!',
			text : 'Ingrese el serial',
			position : 'top',
			icon : 'warning',
			confirmButtonText : 'Cool'
		})
	}else{
		
		//el costo no puede ser mayor a ningun precio de venta
		if(costo > precioMaximo || costo > precioMinimo || costo > precioMayor){
			Swal.fire({
				title : 'Advertencia!',
				text : 'El costo no puede ser mayor a ningun precio de venta',
				position : 'top',
				icon : 'warning',
				confirmButtonText : 'Cool'
			})
		}else{
			 $.post("/compras/ajax/addItemConSerial/", {
				 'idCompra': idCompra,
				 'idArticulo': idArticulo,
				 'costo': costo,
				 'precioMaximo':precioMaximo,
				 'precioMinimo':precioMinimo,
				 'precioMayor':precioMayor,
				 'serial' : serial
		   },function(response){
				$('#responseAddItem').replaceWith(response);
				if($("#responseAddItem").val() == 0){
					Swal.fire({
						title : 'Advertencia!',
						text : 'El articulo no pudo ser agregado a la compra',
						position : 'top',
						icon : 'warning',
						confirmButtonText : 'Cool'
					})
				}else{
					$("#cantidad").val("");
					$("#costo").val("");
					$("#serial").val("");
					focusSelectArticuloNew();
					cargarDetalles();
				}
		 	});
		}
	}
});

$("#btnPagar").click(function(e){
	e.preventDefault();
	//Verificamos que tenga numero de factura
	var no_factura = $("#no_factura").val();
	var precioTotal = parseFloat($("#precioTotal").text());
	var condicionPago = $("#condicionPago").val();
	if(!no_factura){
		Swal.fire({
			title : 'Advertencia!',
			text : 'Ingrese el numero de la factura',
			position : 'top',
			icon : 'warning',
			confirmButtonText : 'Cool'
		})
	}else{
		var idCompra = $("#idCompra").val(); 
		var fechaCompra = $("#fechaCompra").val();
		var idSuplidor = $("#suplidor").val();
		var tipoNCF = $("#tipoNCF").val();
		var ncf = $("#ncf").val();
		var observacion = $("#observacion").val();

		if(precioTotal > 0){
			//Guardamos la compra
			 $.post("/compras/ajax/guardarCompra/", {
				 'idCompra' : idCompra,
				 'fechaCompra': fechaCompra,
				 'no_factura' : no_factura,
				 'idSuplidor' : idSuplidor,
				 'idcondicionPago' : condicionPago,
				 'tipoNCF' : tipoNCF,
				 'ncf' : ncf,
				 'observacion' : observacion,
				 'precioTotal' : precioTotal
		   },function(response){
				$('#responseSave').replaceWith(response);
					Swal.fire({
						title : 'Muy bien!',
						text : 'Se guardo la compra',
						position : 'top',
						icon : 'success',
						confirmButtonText : 'Cool'
					})
					setTimeout(function() {
						location.href = '/compras/create';
					}, 2000);
		   });
		} else{
			Swal.fire({
				title : 'Advertencia!',
				text : 'Debe ingresar al menos 1 articulo',
				position : 'top',
				icon : 'warning',
				confirmButtonText : 'Cool'
			})
		}
	}
});

$("#btnAddPago").click(function(e){
	e.preventDefault();
	var montoPagado = parseFloat($("#totalPago").val());
	var subTotal = parseFloat($("#subTotalPago").val());
	if(montoPagado!=subTotal){
		Swal.fire({
			title : 'Advertencia!',
			text : 'El monto Pagado debe ser igual al Total de la Compra',
			position : 'top',
			icon : 'warning',
			confirmButtonText : 'Cool'
		})
	}else{
		
	}
});

//Carga el detalle de la compra
function cargarDetalles(){
	var idCompra = $("#idCompra").val();
	if(idCompra){
		$('#cuerpoCompra').load("/compras/ajax/loadCuerpo/"+idCompra,function(){});
	}
}

function infoSeriales(idDetalle){
	$('#serialesAcct').load("/compras/ajax/loadInfoDetailSerial/"+idDetalle,function(){
		$("#serialesAcctModal").modal("show");
	});
}

function eliminarItem(idDetalle){
	 $.post("/compras/ajax/eliminarItemSinSerial/", {
		 'idDetalle': idDetalle
	   },function(response){
		   cargarDetalles();
		   focusSelectArticuloNew();
	 });
}

function calculoPago(){
	var cajaPago = parseFloat($("#cajaPago").val());
	var efectivoPago = parseFloat($("#efectivoPago").val());
	var depositoPago = parseFloat($("#depositoPago").val());
	var transferenciaPago = parseFloat($("#transferenciaPago").val());
	var chequePago = parseFloat($("#chequePago").val());
	
	var acumulado = cajaPago+efectivoPago+depositoPago+transferenciaPago+chequePago;
	$("#totalPago").val(acumulado);
}

function focusSelectArticuloNew(){
	$('#articulos').val('').trigger('change.select2');
	$('#articulos').select2('focus');
//	$('#articulos').select2('open');
}