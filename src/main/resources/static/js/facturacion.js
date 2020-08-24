//peticiones iniciales
var incluyeItbis = 0;
var columnas = [];
$('#articulos').load("/articulos/ajax/getAll",function(){
		//verificamos si la factura tiene un cliente asociado
		$("#clienteAcct").load("/articulos/ajax/getClienteAcct/",function(){
			var clienteAcct = $("#actualCliente").val();
			var rncClienteAcct = $("#actualRnc").val();
			if(clienteAcct!="0"){
				$("#selectCliente").val(clienteAcct);
				$("#rncCliente").val(clienteAcct);
			}
			//Verificamos si la factura tiene un vendedor asociado
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
});

var idArticuloBuscado;

//Cuando inicie por primera vez la lista estara oculta
$('#articulos').hide();

$("#selectComprobanteFiscal").change(function(){
	var comprobanteFiscalID = $("#selectComprobanteFiscal").val();
	$("#comprobanteFiscalInfo").load("/articulos/ajax/getComprobanteFiscal/"+comprobanteFiscalID,function(){
		incluyeItbis = $("#incluyeItbis").val();
		factura_detalle_items(incluyeItbis);
	});
});

//evento de teclado para el buscador de articulos
$("#articulo").keyup(function(e) {
	var txtFind = $("#articulo").val();
	txtFind = txtFind.replace(/ /g, "_");
	if (txtFind != "") {
		$('#articulos').load("/articulos/ajax/getAll/" + txtFind);
	} else {
		$('#articulos').load("/articulos/ajax/getAll");
	}
});

//funcion para controlar el producto buscado
function seleccionado(valor, id) {
	idArticuloBuscado = id;
	$("#articulo").val(valor);
	//verificamos el tipo de articulo
	$.get("/articulos/ajax/getType/" + id, function(fragment) {
		$('#tipoArticulo').replaceWith(fragment);
	});
}

//Cuando la lista de productos pierda el foco se oculta la lista
$("#articulos").focusout(
		function() {
			$('#articulos').hide();
			//Datos del articulo seleccionado
			$.get("/articulos/ajax/getInfoArticulo/" + idArticuloBuscado,
					function(fragment) {
						$('#articuloBuscado').replaceWith(fragment);
						$('#cantidadProducto').prop('max', $('#disponibleModal').val());
					});
			//Valores iniciales en 0
			$("#precioRango").val(0);
			$("#cantidadProducto").val(0);
			//Verificamos el tipo del articulo (Con serial, Sin serial)
			if ($("#tipoArticulo").val() == "SI") {
				//llenamos la lista de seriales según el artículo
				$('#addMultiSerial').load(
						"/articulos/ajax/getSerialesForArticulo/"
								+ idArticuloBuscado,function(){
									//Mostramos el modal para agregar seriales
									$('#serialesModal').modal('show');
								});
			} else {
//				$('#articuloModal').modal('show'); //Comentado a peticion del cliente
			}
		});

//Cuando el producto tenga el foco aparece la lista
$("#articulo").focusin(function() {
	$('#articulos').show();
});

//Cuando seleccione alguna cantidad del articulo, el precio cambia segun el articulo (max,min,mayor)
$("#cantidadProducto").on(
		'change keyup',
		function() {
			//Valor actual
			var acct = $("#cantidadProducto").val();
			if (acct == "") {
				$('#cantidadProducto').val(0);
				$('#precioRango').val(0);
			} else {
				var idArticulo = $("#idArticuloBuscado").val();
				//calculamos el precio del articulo según el rango
				//para el calculo del precio verificamos si hay algun cliente seleccionado
				var idCliente = $("#selectCliente").val();
				if(idCliente!=""){
					//Si el cliente esta seleccionado verificamos el precio
					$.get("/articulos/ajax/getPriceWhitClient/" + idArticulo + "/" + acct + "/" + idCliente,
							function(fragment) {
								$('#precioRango').replaceWith(fragment);
								//activamos el input de precio para que se pueda modificar
								$("#precioRango").removeAttr('disabled');
							});
				}else{
					//desactivamos el input para que no se pueda modificar
					$("#precioRango").attr('disabled','disabled');
					$.get("/articulos/ajax/getPrice/" + idArticulo + "/" + acct,
							function(fragment) {
								$('#precioRango').replaceWith(fragment);
							});
				}
			}
		});

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
		});
});

//Evento para agregar articulo a la factura
$("#agregarArticuloFactura").click(function(e) {
	e.preventDefault();
	var idArticulo = idArticuloBuscado;
	var cantidad = $("#cantidadProducto").val();
	var precio = $("#precioRango").val();
	
if(cantidad>0){		
	// obtenemos el valor del itbis
	var valorItbis = 0;
	var pagaItbis = $("#pagaItbis").val();
	var incluyeItbis = $("#incluyeItbis").val();
	var valorItbis = $("#valorItbis").val();
	
	if(incluyeItbis == 1){
		valorItbis = $("#valorItbis").val();
	}
	
	// verificacion para articulos con imei
	if($("#tipoArticulo").val() == "SI"){
		var count = 0;
		var seriales = [];
		// Evento cuando se pasen los seriales a la derecha
		 $('#serialesSeleccionados li').each(function (i) {
	           var index = $(this).index();
	           seriales.push($(this).text());
	           count++;
	       });
		 // Si no selecciona ningun serial mostrar mensaje de alerta
		if(count==0){
			Swal.fire({
				  title: 'Advertencia!',
				  text: 'Debe seleccionar al menos un serial para este articulo',
				  position: 'top',
				  icon: 'warning',
				  confirmButtonText: 'Cool'
			})
		}else{
			// Selecciona al menos un serial
			// validaciones del precio del articulo con serial si tiene cliente
			// seleccionado
			var idCliente = $("#selectCliente").val();
			if(!idCliente){
				idCliente = 0;
			}
			var comprobanteFiscalId = $("#selectComprobanteFiscal").val();
			var temporalPrice = $("#temporalPrice").val();
			var idDetalle = $("#detalleArticuloId").val();
			// verificamos si selecciona varios seriales
				// Si selecciona mas de un serial verificamos si tienen el mismo
				// precio,
				// de lo contrario mostramos modal
				$("#responsePreciosSeriales").load("/articulos/ajax/verificarPreciosDeSeriales/",{
					 'idDetalle': idDetalle,
					 'idArticulo': idArticulo, 
					 'cantidad': cantidad,
					 'precio': precio,
					 'idCliente': idCliente,
					 'realPrice': temporalPrice,
					 'seriales': seriales.toString()
				},function(){
					if($("#autorizado").val()==0){
						var estatusDistinctSerials = $("#estatusDistinctSerials").val();
						if(estatusDistinctSerials == 1){
							$("#ditinctSerialPriceModal").modal("show");
						}else{
							// seriales con precio igual
							// verificamos que el precio no sea menor que el
							// precio minimo
							$("#responsePreciosSerialesNotMinimo").load("/articulos/ajax/verificarPreciosDeSerialesNotMinimo/",{
								 'idDetalle': idDetalle,
								 'idArticulo': idArticulo, 
								 'precio': precio,
								 'cantidad': cantidad,
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
									$("#autorizado").val(1);
								}						
							});
						}
					}
					
					// verificamos si esta autorizado para que agregue el
					// articulo con serial a la factura
					if($("#autorizado").val()==1){
						 $.post("/articulos/ajax/addArticuloConSerial/",
									{
										idArticulo: idArticulo,
										cantidad: cantidad,
									    idCliente: idCliente,
										precio: precio,
										realPrice: temporalPrice,
										comprobanteFiscalId: comprobanteFiscalId,
										seriales: seriales.toString(),
										columnas: columnas.toString()
									},
									function(data, status){
										console.log("Articulo agregado a la facturacion");
										// remover la lista de los seriales
										// seleccionados
										$('#serialesSeleccionados li').remove();
										$("#cantidadProducto").val("0");
										$("#precioRango").val("");
										factura_detalle_items(incluyeItbis);
										$("#autorizado").val(0);
										location.reload();
								});
					}else{
						$("#autorizado").val(0);
					}
				});
			// }
		}
	}else{
		// verificacion para articulos sin Imei
		var nombre = $("#nombreArticuloBuscado").val();
		var minimo = $("#precioMinimo").val();
		minimo = parseFloat(minimo);
		var mayor = $("#precioMayor").val();
		mayor = parseFloat(mayor);
		var conItbis = $("#conItbis").val();
		var disponible = $("#disponibleModal").val();
		var maximo =  $("#precioMaximo").val();
		maximo = parseFloat(maximo);
		var precio = $("#precioRango").val();
		precio = parseFloat(precio);
		var error = 0;
		
		// validaciones del precio del articulo si tiene cliente seleccionado
		var idCliente = $("#selectCliente").val();
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
						  precio: precio,
						  conItbis: conItbis,
						  disponible: disponible,
						  valorItbis: valorItbis,
						  incluyeItbis: incluyeItbis,
						  realPrice: realPrice,
						  maximo: maximo
						},
						function(data, status){
							console.log("Articulo agregado a la facturacion");
							$("#cantidadProducto").val("0");
							$("#precioRango").val("");
							factura_detalle_items(incluyeItbis);
					});
		}
	}
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
			$("#precioRango").val(sumaSeriales);
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

$("#guardarSerialesDelProducto").click(function(e) {
	var count = 0;
	var seriales = [];
	//Evento cuando se pasen los seriales a la derecha
	 $('#serialesSeleccionados li').each(function (i) {
           var index = $(this).index();
           seriales.push($(this).text());
           count++;
       });
	 var clienteId = $("#selectCliente").val();
	 if(!clienteId){
		 clienteId = 0;
	 }
	 $("#cantidadYprecio").load("/articulos/ajax/obtenerInfoDeSeriales/", { 
		 'clienteId': clienteId, 
         'seriales': seriales.toString()
    },function() {
		//habilitamos que pueda modificar el precio
		$("#precioRango").removeAttr('disabled');
		$("#temporalPrice").val($("#precioRango").val());
    });
});


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
						$.get("/facturas/download/"+invoiceId,function(data,status,xhr){
							factura_detalle_items(incluyeItbis);
							$("#pagoModal").modal("hide");
							var a = document.createElement('a');
							  a.target="_blank";
							  a.href='/facturas/download/'+invoiceId;
							  a.click();
							setTimeout(function() {
								location.href = '/facturas/';
							}, 3000);
						});
					}
			 	});
			}
		}
	}
});









