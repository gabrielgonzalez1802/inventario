//peticiones iniciales
var incluyeItbis = 0;
var columnas = [];
var seleccionado = $("#articulos").select2();
//al principio tomara el foco
focusSelectArticuloNew();
//verificamos si la factura tiene un cliente asociado
$("#clienteAcct").load("/articulos/ajax/getClienteAcct/",function(){
	var clienteAcct = $("#actualCliente").val();
	var rncClienteAcct = $("#actualRnc").val();
	if(clienteAcct!="0"){
		$("#selectCliente").val(clienteAcct);
		$("#rncCliente").val(clienteAcct);
	}
	// Verificamos si la factura tiene un vendedor asociado
	$("#vendedorAcct").load("/articulos/ajax/getVendedorAcct/",function(){
		var vendedorAcct = $("#actualVendedor").val();
		if(vendedorAcct!="0"){
			$("#selectVendedor").val(vendedorAcct);
		}
		var idComprobanteFiscal = $("#selectComprobanteFiscal").val();
		if(idComprobanteFiscal){
			$("#comprobanteFiscalInfo").load("/articulos/ajax/getComprobanteFiscal/"+idComprobanteFiscal,function(){
				incluyeItbis = $("#incluyeItbis").val();
				factura_detalle_items(incluyeItbis);
			});
		}
	});
});
		
var idArticuloBuscado;

$("#selectComprobanteFiscal").change(function(){
	var comprobanteFiscalID = $("#selectComprobanteFiscal").val();
	$("#comprobanteFiscalInfo").load("/articulos/ajax/getComprobanteFiscal/"+comprobanteFiscalID,function(){
		incluyeItbis = $("#incluyeItbis").val();
		factura_detalle_items(incluyeItbis);
	});
});

//Evento cuando se seleccine un articulo
$('#articulos').on("select2:select", function(e) { 
	if(seleccionado.val()!=""){
		idArticuloBuscado = seleccionado.val();
		var valor = $('#articulos :selected').text();
		$("#articulo").val(valor);
//	    alert("seleccione: "+seleccionado.val()+" texto: "+$('#articulos :selected').text());
			//Datos del articulo seleccionado
			$.get("/articulos/ajax/getInfoArticulo/" + idArticuloBuscado,
					function(fragment) {
						$('#articuloBuscado').replaceWith(fragment);
						idArticuloBuscado = $("#articulos").val();
						$("#nombreArticuloSerial").val("");
						//validamos el tipo de articulo
						if ($("#tipoArticulo").val() != "SI") {
							$('#cantidadSinSerial').prop('max', $('#disponibleModal').val());
						}
						
						//Verificamos el tipo del articulo (Con serial, Sin serial)
						if ($("#tipoArticulo").val() == "SI") {
							//llenamos la lista de seriales según el artículo
							$('#addMultiSerial').load(
									"/articulos/ajax/getSerialesForArticulo/"
											+ idArticuloBuscado,function(){
												//Mostramos el modal para agregar seriales
												var nombreArticulo = $("#articulo").val();
												let indice = nombreArticulo.indexOf("-");
												// Cortar desde 0 hasta la aparición del primer -
												let extraida = nombreArticulo.substring(0, indice);
												$("#nombreArticuloSerial").val(extraida);
												$('#serialesModal').modal('show');
											});
						} else {
							$("#cantidadSinSerial").val(1);
							var cantidad = $("#cantidadSinSerial").val();
							var idArticulo = $("#idArticuloBuscado").val();
//							//para el calculo del precio verificamos si hay algun cliente seleccionado
							var idCliente = $("#selectCliente").val();
							if(idCliente!=""){
//								//Si el cliente esta seleccionado verificamos el precio
								$.get("/articulos/ajax/getPriceWhitClient/" + idArticulo + "/" + cantidad + "/" + idCliente,
										function(fragment) {
											$('#precioSinSerial').replaceWith(fragment);
											var total = $("#cantidadSinSerial").val() * $('#precioSinSerial').val();
											$("#totalPrecioSinSerial").val(total);
										});
							}else{
								$.get("/articulos/ajax/getPrice/" + idArticulo + "/" + cantidad,
										function(fragment) {
											$('#precioSinSerial').replaceWith(fragment);
											var total = $("#cantidadSinSerial").val() * $('#precioSinSerial').val();
											$("#totalPrecioSinSerial").val(total);
										});
							}
							$('#articuloModal').modal('show');
							$('#cantidadSinSerial').focus();
						}
					});
	}
});

function cambioCantidadSinSerial() {
	var cantidad = $("#cantidadSinSerial").val();
	var idArticulo = $("#idArticuloBuscado").val();
	//para el calculo del precio verificamos si hay algun cliente seleccionado
	var idCliente = $("#selectCliente").val();
	if(idCliente!=""){
		//Si el cliente esta seleccionado verificamos el precio
		$.get("/articulos/ajax/getPriceWhitClient/" + idArticulo + "/" + cantidad + "/" + idCliente,
				function(fragment) {
					$('#precioSinSerial').replaceWith(fragment);
					var total = $("#cantidadSinSerial").val() * $('#precioSinSerial').val();
					$("#totalPrecioSinSerial").val(total);
				});
	}else{
		$.get("/articulos/ajax/getPrice/" + idArticulo + "/" + cantidad,
				function(fragment) {
					$('#precioSinSerial').replaceWith(fragment);
					var total = $("#cantidadSinSerial").val() * $('#precioSinSerial').val();
					$("#totalPrecioSinSerial").val(total);
					if(total=="0"){
						Swal.fire({
							  title: 'Advertencia!',
							  text: 'La cantidad seleccionada no se encuentra en el rango de precios del articulo, Puede ajustar el precio manualmente',
							  position: 'top',
							  icon: 'warning',
							  confirmButtonText: 'Cool'
						})
					}
				});
	}
}

function addSerialList(){
	var count = 0;
	var countActive = 0;
	var seriales = [];
	var serialesActive = [];
	var total = $("#totalPrecioConSerial").val();
	var temporalPrice = $("#precioConSerial").val();
	if(total==""){
		total = 0;
	}
	if(temporalPrice==""){
		temporalPrice = 0;
	}
	//verificamos los seriales seleccionados para agregar y calcular el precio
	 $('#addMultiSerial li').each(function (i) {
		  countActive += $(this).hasClass("active");
          var index = $(this).index();
          seriales.push($(this).text());
		  if($(this).hasClass("active")){
			  serialesActive.push($(this).text());
		  }
           count++;
     });
	 var clienteId = $("#selectCliente").val();
	 if(!clienteId){
		 clienteId = 0;
	 }
	 
	 //calculamos el precio de los seriales preseleccionados
//	alert("total activos para guardar: "+countActive);

	 $("#cantidadYprecio").load("/articulos/ajax/obtenerInfoDeSeriales/", { 
		 'clienteId': clienteId, 
         'seriales': serialesActive.toString()
    },function() {
    	var newPrice = parseFloat(temporalPrice) + parseFloat($("#precioConSerial").val());
    	$("#nombreArticuloSerial").val($("#articulo").val());
    	$("#precioConSerial").val(newPrice);
		$("#totalPrecioConSerial").val(newPrice);
    });
}

function minusSerialList(){
	var count = 0;
	var countActive = 0;
	var seriales = [];
	var serialesActive = [];
	var total = $("#totalPrecioConSerial").val();
	var temporalPrice = $("#precioConSerial").val();
	if(total==""){
		total = 0;
	}
	if(temporalPrice==""){
		temporalPrice = 0;
	}
	//verificamos los seriales seleccionados para agregar y calcular el precio
	 $('#serialesSeleccionados li').each(function (i) {
		  countActive += $(this).hasClass("active");
          var index = $(this).index();
          seriales.push($(this).text());
		  if($(this).hasClass("active")){
			  serialesActive.push($(this).text());
		  }
           count++;
     });
	 var clienteId = $("#selectCliente").val();
	 if(!clienteId){
		 clienteId = 0;
	 }
	 
	 //calculamos el precio de los seriales preseleccionados
//	alert("total activos para guardar: "+countActive);

	 $("#cantidadYprecio").load("/articulos/ajax/obtenerInfoDeSeriales/", { 
		 'clienteId': clienteId, 
         'seriales': serialesActive.toString()
    },function() {
    	var newPrice = parseFloat(temporalPrice) - parseFloat($("#precioConSerial").val());
    	$("#precioConSerial").val(newPrice);
		$("#totalPrecioConSerial").val(newPrice);
		$("#nombreArticuloSerial").val($("#articulo").val());
    });
}

function cambioPrecioSinSerial() {
	var cantidad = $("#cantidadSinSerial").val();
	var idArticulo = $("#idArticuloBuscado").val();
	var total = $("#cantidadSinSerial").val() * $('#precioSinSerial').val();
	$("#totalPrecioSinSerial").val(total);
}

//Cambiar vendedor
$("#selectVendedor").change(function(){
	 var vendedorAcct = $("#selectVendedor").val();
	 if(vendedorAcct==""){
		 vendedorAcct = 0;
	 }
	 $.post("/facturas/ajax/updateVendedorFactura/", {
		 'vendedorID' : vendedorAcct
	 },function(data){
		 console.log("vendedor cambiado");
	 });
});

//cuando seleccione algun cliente obtenemos toda su informacion
function seleccionarCliente(){
	var idCliente = $("#selectCliente").val();	
	if(idCliente==""){
		$("#rncCliente").val("");
		$("#precioRango").val("");
		$("#cantidadProducto").val("0");
		$("#precioRango").attr('disabled','disabled');
		$.get("/articulos/ajax/getInfoCliente/" + 0,
			function(fragment) {
				$("#facturaCliente").val("");
				$("#facturaTelefono").val("");
				$("#facturaRnc").val("");
			});
	}else{
		//obtenemos la informacion del cliente
		$.get("/articulos/ajax/getInfoCliente/" + idCliente,
				function(fragment) {
					$('#nuevoCliente').replaceWith(fragment);
					//Actualizamos el cliente en el formulario de pago
					$("#facturaCliente").val($("#nombreCliente").val());
					$("#facturaTelefono").val($("#telefonoCliente").val());
					$("#facturaRnc").val($("#rncCliente").val());
					//actualizamos el precio
					var articulo = $("#articulo").val();
					if(articulo!=""){
						//validacion para articulos sin serial
						if ($("#tipoArticulo").val() != "SI") {
							//Actualizamos precio
							$.get("/articulos/ajax/getPriceWhitClient/" + idArticuloBuscado + "/" + 1 + "/" + idCliente,
							function(fragment) {
								$('#precioRango').replaceWith(fragment);
								//activamos el input de precio para que se pueda modificar
								$("#precioRango").removeAttr('disabled');
							});
						}else{
							//validacion para articulos con serial
							//Actualizamos precio
							$.get("/articulos/ajax/getPriceWhitClientWhitSerial/" + idArticuloBuscado + "/" + 1 + "/" + idCliente,
							function(fragment) {
								$('#precioRango').replaceWith(fragment);
								//activamos el input de precio para que se pueda modificar
								$("#precioRango").removeAttr('disabled');
							});
						}
					}
				});
	}
}

//accion para llamar al modal de agregar servicio
$("#btnServiceModal").click(function() {
	$('#servicioModal').modal('show');
});

$("#formTop").submit(function(e) {
	e.preventDefault();
});

//accion para llamar al modal de agregar cliente
$("#btnClienteModal").click(function() {
   $('#clienteModal').modal('show');
});

//Evento para agregar servicio
$("#btnAddService").click(function(e) {
	e.preventDefault();
	var descripcion = $('#descripcionServicio').val();
	var cantidad = $('#cantidadServicio').val();
	var costo = $('#costoServicio').val();
	var precio = $('#precioServicio').val();
	
	if (!descripcion) {
		Swal.fire({
			  title: 'Advertencia!',
			  text: 'La descripcion debe estar llena',
			  position: 'top',
			  icon: 'warning',
			  confirmButtonText: 'Cool'
		})
		return false;
	}
	
	if (!costo || !precio || !cantidad) {
			Swal.fire({
			title : 'Advertencia!',
			text : 'Todos los campos deben estar llenos',
			position : 'top',
			icon : 'warning',
			confirmButtonText : 'Cool'
		})
		return false;
	}
	
	//agregar servicios
	  $.post("/articulos/ajax/addService/",
		{
		  descripcion: descripcion,
		  costo: costo,
		  precio: precio,
		  cantidad: cantidad
		},
		function(data, status){
			console.log("Servicio guardado");
			factura_detalle_items(incluyeItbis);
			servicio_limpiar();
			focusSelectArticuloNew();
		});
});

//Evento agregar articulo Sin serial a la factura
function agregarArticuloSinSerial(){
	var idArticulo = $("#idArticuloBuscado").val();
	var cantidad = $("#cantidadSinSerial").val();
	var total = $("#totalPrecioSinSerial").val();
	var idCliente = $("#selectCliente").val();
	
	if(cantidad!=""){
		// obtenemos el valor del itbis
		var valorItbis = 0;
		var pagaItbis = $("#pagaItbis").val();
		var incluyeItbis = $("#incluyeItbis").val();
		var valorItbis = $("#valorItbis").val();
		
		if(incluyeItbis == 1){
			valorItbis = $("#valorItbis").val();
		}

		var nombre = $("#nombreArticuloBuscado").val();
		var minimo = $("#precioMinimo").val();
		minimo = parseFloat(minimo);
		var mayor = $("#precioMayor").val();
		mayor = parseFloat(mayor);
		var conItbis = $("#conItbis").val();
		var disponible = $("#disponibleModal").val();
		var maximo =  $("#precioMaximo").val();
		maximo = parseFloat(maximo);
		var precio = $("#totalPrecioSinSerial").val();
		precio = parseFloat(precio);
		var error = 0;
		
		// validaciones del precio del articulo si tiene cliente seleccionado
		if(idCliente!=""){
			var precioCliente = $("#precioCliente").val();
			// verificamos el precio del cliente y hacemos las comparaciones
			if(precioCliente == "precio_1"){
				if(precio>=maximo){
					error=0;
				}else{
					// si el cliente tiene precio maximo se puede bajar al
					// minimo pero nunca menos de ahi
					// a menos que se cumpla la condicion de cantidad
					if(precio==minimo){
						error=0;
					}else{
						if(precio<minimo){
							Swal.fire({
								  title: 'Advertencia!',
								  text: 'El precio no puede ser menor al precio minimo',
								  position: 'top',
								  icon: 'warning',
								  confirmButtonText: 'Cool'
							})
							error++;
						}
					}
				}
			}
			
			if(precioCliente == "precio_2"){
				if(precio>=minimo){
					error=0;
				}else{
					Swal.fire({
						  title: 'Advertencia!',
						  text: 'El precio no puede ser menor al precio minimo',
						  position: 'top',
						  icon: 'warning',
						  confirmButtonText: 'Cool'
					})
					error++;
				}
			}
			
			if(precioCliente == "precio_3"){
				if(precio>=mayor){
					error=0;
				}else{
					Swal.fire({
						  title: 'Advertencia!',
						  text: 'El precio no puede ser menor al precio x mayor',
						  position: 'top',
						  icon: 'warning',
						  confirmButtonText: 'Cool'
					})
					error++;
				}
			}
		}else{
			//Si no tiene cliente seleccionado se hace las comparaciones por el precio mayor y solo puede bajar hasta el minimo
			if(precio>=mayor){
				error=0;
			}else{
				//verificamos que sea mayor o igual al minimo
				if(precio>=minimo){
					error=0;
				}else{
					Swal.fire({
						  title: 'Advertencia!',
						  text: 'El precio no puede ser menor al precio minimo',
						  position: 'top',
						  icon: 'warning',
						  confirmButtonText: 'Cool'
					})
					error++;
				}
			}
		}

		// si no hay errores agregamos el registro
		if(error==0){
			  // verificamos si el comprobante fiscal paga itbis
			 if(pagaItbis==1){
				 conItbis = "SI";
				 // verificamos si el comprobante incluye itbis en el precio
				 var realPrice = precio;
				 if(incluyeItbis==1){
					 precio = precio - (precio * (valorItbis/100));
				 }
			 }else{
				 valorItbis=0;
				 var realPrice = 0;
			 }
			  $.post("/articulos/ajax/addArticuloSinSerial/",
						{
						  idArticulo: idArticulo,
						  cantidad: cantidad,
						  precioAcct: precio,
						  conItbis: conItbis,
						  disponible: disponible,
						  valorItbis: valorItbis,
						  incluyeItbis: incluyeItbis,
						  realPrice: realPrice,
						  maximo: maximo
						},
						function(data, status){
							console.log("Articulo agregado a la facturacion");
							$("#cantidadSinSerial").val("");
							$("#precioSinSerial").val("");
							$("#totalPrecioSinSerial").val("");
							$("#articuloModal").modal("hide");
							factura_detalle_items(incluyeItbis);
							focusSelectArticuloNew();
					});
		}
	}
}

$("#guardarSerialesDelProducto").click(function(e){
	e.preventDefault();
	var idArticulo = idArticuloBuscado;
	var count = 0;
	var seriales = [];
	var pagaItbis = $("#pagaItbis").val();
	var incluyeItbis = $("#incluyeItbis").val();
	var valorItbis = 0;
	var articulo = $("#nombreArticuloSerial").val();
	if(incluyeItbis == 1){
		valorItbis = $("#valorItbis").val();
	}
	var precio =  $("#precioConSerial").val();
	var total = $("#totalPrecioConSerial").val();
	
	 $('#serialesSeleccionados li').each(function (i) {
	    var index = $(this).index();
	    seriales.push($(this).text());
	    count++;
	});
	
	if(count>0){	
		// validaciones del precio del articulo con serial si tiene cliente seleccionado
		var idCliente = $("#selectCliente").val();
		if(!idCliente){
			idCliente = 0;
		}
		var comprobanteFiscalId = $("#selectComprobanteFiscal").val();
		var temporalPrice = $("#temporalPrice").val();
		
		if($("#autorizado").val()!=1){
			//Si selecciona mas de un serial verificamos si tienen el mismo precio
			$("#responsePreciosSeriales").load("/articulos/ajax/verificarPreciosDeSeriales/",{
				 'idArticulo': idArticulo, 
				 'cantidad': count,
				 'precio': precio,
				 'idCliente': idCliente,
				 'realPrice': total,
				 'seriales': seriales.toString()
			},function(){
				var estatusDistinctSerials = $("#estatusDistinctSerials").val();
				//si los seriales tienen distinto precio mostramos el modal para perzonalizar valores
				if(estatusDistinctSerials == 1){
					$("#ditinctSerialPriceModal").modal("show");
				}else{
					// seriales con precio igual. verificamos que el precio no sea menor que el precio minimo
					$("#responsePreciosSerialesNotMinimo").load("/articulos/ajax/verificarPreciosDeSerialesNotMinimo/",{
						 'idArticulo': idArticulo, 
						 'precio': precio,
						 'cantidad': count,
						 'idCliente': idCliente,
						 'seriales': seriales.toString()
					},function(){
						// si la validacion es correcta autorizamos
						var estatus = $("#estatusUpdateSerialNotMinimo").val();
						if(estatus == "1"){
							// es menor
							Swal.fire({
								  title: 'Advertencia!',
								  text: 'El precio no puede ser menor al precio minimo',
								  position: 'top',
								  icon: 'warning',
								  confirmButtonText: 'Cool'
							})
						}else{
							 $.post("/articulos/ajax/addArticuloConSerial/",
										{
											idArticulo: idArticulo,
											cantidad: count,
										    idCliente: idCliente,
											precio: precio,
											realPrice: total,
											comprobanteFiscalId: comprobanteFiscalId,
											seriales: seriales.toString(),
											columnas: columnas.toString()
										},
										function(data, status){
											location.reload();
							  });
						}	
					});
				}
			});
		}else{
			 $.post("/articulos/ajax/addArticuloConSerial/",
						{
							idArticulo: idArticulo,
							cantidad: count,
						    idCliente: idCliente,
							precio: precio,
							realPrice: total,
							comprobanteFiscalId: comprobanteFiscalId,
							seriales: seriales.toString(),
							columnas: columnas.toString()
						},
						function(data, status){
							location.reload();
			  });
		}
	}else{
		Swal.fire({
			  title: 'Advertencia!',
			  text: 'Debe seleccionar al menos un serial para este articulo',
			  position: 'top',
			  icon: 'warning',
			  confirmButtonText: 'Cool'
		})
	}
});

$("#guardarCostoSeriales").click(function(e) {
	e.preventDefault();	
	
	$('#responsePreciosSeriales').find('input').each(function() {
		  console.log($(this).val());
		  columnas.push($(this).val());
		});
	
	var longitud = columnas.length;
		
	var idCliente = $("#selectCliente").val();
	if(!idCliente){
		idCliente = 0;
	}
	
	//Verificamos los Precios,
	//Condiciones:  *pueden ser mayores
	//				*pueden ser menor pero solo hasta el precio minimo 
	$("#estatusModificarSeriales").load("/articulos/ajax/modificarPreciosDeSeriales/",{
		 'idCliente': idCliente,
		 'infoSeriales': columnas.toString()
	},function(){
		var estatusModificarSeriales = $("#estatusUpdateSerial").val();
		if(estatusModificarSeriales==0){
			Swal.fire({
				title : 'Advertencia!',
				text : 'Los precios no pueden ser menor al precio minimo',
				position : 'top',
				icon : 'warning',
				confirmButtonText : 'Cool'
			})
			$("#autorizado").val(0);
		}else{
			var sumaSeriales = $("#sumaSeriales").val();
			$("#precioConSerial").val(sumaSeriales);
			$("#ditinctSerialPriceModal").modal("hide");
			$("#autorizado").val(1);
		}
	});
});

//Carga el detalle de la factura
function factura_detalle_items(incluyeItbis){
	$('#cuerpoFactura').load("/articulos/ajax/loadCuerpoFactura/"+incluyeItbis,function(){
		$('#articulo').val("");
		//actualizamos el precio en el formulario de pago
		var precioTotal = $('#precioTotal').text();
		$('#montoFactura').val(precioTotal);
		//Si el precio total es 0 ocultamos el boton de pago
		if(precioTotal=="0.0"){
			$('#divButtonSaveFact').hide();
		}else{
			$('#divButtonSaveFact').show();
		}
	});
}

//Limpiar valores del modal de servicios
function servicio_limpiar(){
	$('#descripcionServicio').val("");
	$('#cantidadServicio').val("");
	$('#costoServicio').val("");
	$('#precioServicio').val("");
	$('#precioServicio').val("");
	$('#servicioModal').modal('hide');
}

//Elimina un servicio de la factura temporal
function eliminarServicioFactura(id){
	if(id){
		Swal.fire({
			  title: 'Quiere borrar el servicio?',
			  icon: 'warning',
			  position: 'top',
			  showCancelButton: true,
			  confirmButtonColor: '#3085d6',
			  cancelButtonColor: '#d33',
			  confirmButtonText: 'Si, Borrar!'
			}).then((result) => {
			  if (result.value) {
				 $.post("/articulos/ajax/deleteService/",
					{
						idServicio: id
					},function(data, status){
						console.log("Servicio eliminado de la facturacion");
						factura_detalle_items(incluyeItbis);
						$('#articulo').val("");
					});  
			  }
			})
	}
}

function modalSerialesDetalleFactura(IdfacturaDetalle){
	$('#serialesAcct').load("/articulos/ajax/obtenerSeriales/"+IdfacturaDetalle,function(){
		$("#serialesAcctModal").modal("show");
	});
}

function eliminarSerialFactura(idSerial){
	if(idSerial){
		Swal.fire({
			  title: 'Quiere borrar el serial?',
			  icon: 'warning',
			  position: 'top',
			  showCancelButton: true,
			  confirmButtonColor: '#3085d6',
			  cancelButtonColor: '#d33',
			  confirmButtonText: 'Si, Borrar!'
			}).then((result) => {
			  if (result.value) {
				 $.post("/articulos/ajax/deleteSerial/",
					{
					  idSerial: idSerial
					},function(data, status){
						console.log("Serial eliminado de la facturacion");
						factura_detalle_items(incluyeItbis);
					});  
			  }
			})
		$("#serialesAcctModal").modal("hide");
		factura_detalle_items(incluyeItbis);
	}
}

var campo = false; //variable para controlar cuando se añada un campo

//Evento al presionar clic en boton del modal para agregar serial
$("#btnAddSerial").click(function(e) {
	e.preventDefault();
	if(!campo){
		campo = true
		var tempCampo = '<div class="col col-md-12" id="containerDinamicAddSerial"><div class="form-group">'+
		'<label>Agregar Serial</label> <input class="form-control" id="inputAddSerial" type="text"'+
		'placeholder="Ingrese el serial"></div></div>';
		$("#rowAddSerial").append(tempCampo);
	}else{
		//verificamos que escriba un serial
		var idDetalle = $("#detalleArticuloId").val();
		var newSerial = $("#inputAddSerial").val();
		if(newSerial){
			// verificamos si el serial existe. De existir lo agregamos, de lo contrario msj de error
	        $("#responseAddSerial").load("/articulos/ajax/addSerialArticuloFactura/", { 
	            'idDetalle': idDetalle, 
	            'serial': newSerial
	           },function() {
	   			// verificamos la respuesta
	   			var response = $("#AddSerialStatus").val();
	   			if(response){
	   				if(response == "GUARDADO"){
	   					$("#containerDinamicAddSerial").remove();
	   					$('#serialesAcct').load("/articulos/ajax/obtenerSeriales/"+idDetalle);
	   					factura_detalle_items(incluyeItbis);
	   				}
	   				if(response == "EXISTE EN DETALLE"){
	   					Swal.fire({
	   						title : 'Advertencia!',
	   						text : 'El serial se encuentra asociado a la factura',
	   						position : 'top',
	   						icon : 'warning',
	   						confirmButtonText : 'Cool'
	   					})
	   					$("#containerDinamicAddSerial").remove();
	   				}
	   				if(response == "NO EXISTE EN ORIGINAL"){
	   					Swal.fire({
	   						title : 'Advertencia!',
	   						text : 'El serial no existe',
	   						position : 'top',
	   						icon : 'warning',
	   						confirmButtonText : 'Cool'
	   					})
	   					$("#containerDinamicAddSerial").remove();
	   				}
	   			}
				campo = false;
	        });
		}
	}
});

function eliminarArticuloFactura(idDetalle){
	if(idDetalle){
		Swal.fire({
			  title: 'Quiere borrar este registro de la factura?',
			  icon: 'warning',
			  position: 'top',
			  showCancelButton: true,
			  confirmButtonColor: '#3085d6',
			  cancelButtonColor: '#d33',
			  confirmButtonText: 'Si, Borrar!'
			}).then((result) => {
			  if (result.value) {
				  $("#responseDeleteArticulo").load("/articulos/ajax/deleteArticuloFactura/", { 
			            'idDetalle': idDetalle
			       },function() {
			    	   if( $("#DeleteArticuloStatus").val() == 1){
			    		   $("#DeleteArticuloStatus").val("");
			    		   factura_detalle_items(incluyeItbis);
			    	   }
			    });
			  }
			})
	}
}

$("#btnNCF").click(function(e) {
	$("#ncfModal").modal("show");
});

$("#btnTallerModal").click(function(e) {
	$("#tallerModal").modal("show");
});

$("#selectCondicionPago").change(function(){
	var condicionPago = $("#selectCondicionPago").val();
	 $.post("/facturas/ajax/updateCondicionPagoFactura/", {
		 'condicionPagoID' : condicionPago
	 },function(data){
		 console.log("condicion cambiada");
	 });
});

//guardar taller
$("#btnAddTaller").click(function(e) {
	 $.post("/talleres/ajax/addRecepcion/", $("#taller").serialize(),
		function(data, status){
		 	$('#responseAddRecepcion').replaceWith(data);
		 	if($('#responseAddRecepcion').val()==1){
		 		Swal.fire({
						title : 'Muy bien!',
						text : 'Registro guardado',
						position : 'top',
						icon : 'success',
						confirmButtonText : 'Cool'
					})
					limpiarTaller();
		 			$("#tallerModal").modal("hide");
		 	}else{
		 		Swal.fire({
						title : 'Advertencia!',
						text : 'El serial el registro no pudo ser guardado',
						position : 'top',
						icon : 'warning',
						confirmButtonText : 'Cool'
					})
		 	}
		});
	 
});

function limpiarTaller(){
	$("#celularTaller").val("");
	$("#nombreTaller").val("");
	$("#cedulaTaller").val("");
	$("#telefonoTaller").val("");
	$("#serialTaller").val("");
	$("#marcaTaller").val("");
	$("#modeloTaller").val("");
	$("#problemaTaller").val("");
	$("#avanceTaller").val("");
}

$(document).on('keydown', null, 'f8', function(){
	//cargamos los detalles de pago en la factura
	if($("#guardarFactura").is(':visible')){
		factura_pagos_detalle_items();
	}
});     
$("#guardarFactura").click(function(e) {
	e.preventDefault();
	//cargamos los detalles de pago en la factura
	factura_pagos_detalle_items();
});

//Carga el detalle de pago en la factura
function factura_pagos_detalle_items(){
	var precioTotal = $("#precioTotal").text();
	$('#cuerpoPago').load("/articulos/ajax/loadCuerpoFacturaPago/"+precioTotal,function(){
		if($("#mostrarCambio").val()==0){
			$("#totalCanbioPagos").hide();
		}else{
			$("#totalCanbioPagos").show();
		}
		$("#pagoModal").modal("show");
	});
}

function factura_pagos_detalle_itemsNotModalOpen(){
	var precioTotal = $("#precioTotal").text();
	$('#cuerpoPago').load("/articulos/ajax/loadCuerpoFacturaPago/"+precioTotal,function(){
		if($("#mostrarCambio").val()==0){
			$("#totalCanbioPagos").hide();
		}else{
			$("#totalCanbioPagos").show();
		}
	});
}

$("#btnAgregarPago").click(function(e) {
	e.preventDefault();
	var formaPago = $("#selectFormaPago").val();
	var montoFormaPago = $("#montoFormaPago").val();
	if(montoFormaPago<0){
		Swal.fire({
			title : 'Advertencia!',
			text : 'El monto no puede ser negativo',
			position : 'top',
			icon : 'warning',
			confirmButtonText : 'Cool'
		})
		$("#montoFormaPago").val("");
	}else{
		 $.post("/formasPago/ajax/addPagoFactura/", {
				 'formaPago' : formaPago,
				 'montoFormaPago': montoFormaPago,
				 'montoTotal': $("#precioTotal").text()
		   },function(response){
				$('#responseAddPago').replaceWith(response);
				if($("#responseAddPago").val() == 0){
					Swal.fire({
						title : 'Advertencia!',
						text : 'El monto no puede ser mayor al total de la factura',
						position : 'top',
						icon : 'warning',
						confirmButtonText : 'Cool'
					})
				}
				$("#montoFormaPago").val("");
				factura_pagos_detalle_itemsNotModalOpen();
		 	});
	}
});

function eliminarPagoFactura(idPago){
	 $.post("/formasPago/ajax/deletePagoFactura/", {
		 'pago' : idPago
   },function(response){
		factura_pagos_detalle_itemsNotModalOpen();
		$("#montoFormaPago").val("");
 	});
}

$("#btnGuardarFactura").click(function(e) {
	e.preventDefault();
	//verificamos que la factura tenga los pagos completos para proceder a guardarla
	if($("#totalRestaPagos").text()>0){
		Swal.fire({
			title : 'Advertencia!',
			text : 'No se puede guardar la factura hasta completarse los pagos',
			position : 'top',
			icon : 'warning',
			confirmButtonText : 'Cool'
		})
	}else{
		//validacion, debe incluir cliente
		if($("#facturaCliente").val()==""){
			Swal.fire({
				title : 'Advertencia!',
				text : 'El cliente no puede estar vacio',
				position : 'top',
				icon : 'warning',
				confirmButtonText : 'Cool'
			})
		}else{
			//debe incluir RNC
			if($("#facturaRnc").val()==""){
				Swal.fire({
					title : 'Advertencia!',
					text : 'El RNC es necesario para guardar la factura',
					position : 'top',
					icon : 'warning',
					confirmButtonText : 'Cool'
				})
			}else{
				var total_venta = $("#precioTotal").text();
				var total_itbis = $("#subTotalItbis").text();
				var nombreCliente = $("#facturaCliente").val();
				var telefonoCliente = $("#facturaTelefono").val();
				var rncCliente = $("#facturaRnc").val();
				//proceso de guardado de la factura
				 $.post("/facturas/ajax/guardarFactura/",
						{
					 		'total_venta':total_venta,
					 		'nombreCliente':nombreCliente,
					 		'telefonoCliente':telefonoCliente,
					 		'rncCliente':rncCliente,
					 		'total_itbis':total_itbis
						},function(response){
					$('#responseGeneratedInvoice').replaceWith(response);
					var invoiceId = $('#responseGeneratedInvoice').val();
					if(invoiceId>0){
						factura_detalle_items(incluyeItbis);
						$("#pagoModal").modal("hide");
						var a = document.createElement('a');
						  a.target="_blank";
						  a.href='/facturas/print/'+invoiceId;
						  a.click();
						setTimeout(function() {
							location.href = '/facturas/';
						}, 1000);
					}
			 	});
			}
		}
	}
});

function focusSelectArticuloNew(){
	$('#articulos').val('').trigger('change.select2');
	$('#articulos').select2('focus');
//	$('#articulos').select2('open');
}









