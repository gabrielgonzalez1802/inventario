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
				$("#cantidad").val(1);
				$("#costo").val($("#infoCosto").val());
				if($("#infoSerial").val()==1){
					//Con serial
					$("#lblCantidad").hide();
					$("#cantidad").hide();
					$("#agregarItem").hide();
					var costo = parseFloat($("#costo").val());
					$("#serialModal").modal("show");
				}else{
					//Sin serial
					$("#agregarItem").show();
					$("#lblCantidad").show();
					$("#cantidad").show();
				}
			});
		}
	}else{
		$("#cantidad").val("");
		$("#costo").val("");
	}
});

$("#agregarItem").click(function(e){
	e.preventDefault();
	var idCompra = $("#idCompra").val();
	var idArticulo = $("#infoIdArticulo").val();
	var costo = $("#costo").val();
	//Verificamos el tipo de articulo
	if($("#infoSerial").val()==1){
		//Con serial
	}else{
		var cantidad = $("#cantidad").val();
		//Sin serial
		 $.post("/compras/ajax/addItemSinSerial/", {
			 'idCompra': idCompra,
			 'idArticulo': idArticulo,
			 'cantidad': cantidad,
			 'costo': costo
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
				focusSelectArticuloNew();
				cargarDetalles();
			}
	 	});
	}
});

$("#btnAddSerial").click(function(e){
	e.preventDefault();
	var serial = $("#serial").val();
	var idArticulo = $("#infoIdArticulo").val();
	var costo = $("#costo").val();
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
		 $.post("/compras/ajax/addItemConSerial/", {
			 'idCompra': idCompra,
			 'idArticulo': idArticulo,
			 'costo': costo,
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
					}, 1000);
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