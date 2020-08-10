$('#articulos').load("/articulos/ajax/getAll");

var idArticuloBuscado;

factura_detalle_items();

//Cuando inicie por primera vez la lista estara oculta
$('#articulos').hide();

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
								+ idArticuloBuscado);
				//Mostramos el modal para agregar seriales
				$('#serialesModal').modal('show');
			} else {
				$('#articuloModal').modal('show');
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
				$.get("/articulos/ajax/getPrice/" + idArticulo + "/" + acct,
						function(fragment) {
							$('#precioRango').replaceWith(fragment);
						});
			}
		});

//accion para llamar al modal de agregar servicio
$("#btnServiceModal").click(function() {
	$('#servicioModal').modal('show');
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
			factura_detalle_items();
			servicio_limpiar();
		});
});

//Evento para agregar articulo a la factura
$("#agregarArticuloFactura").click(function(e) {
	e.preventDefault();
	var idArticulo = idArticuloBuscado;
	var cantidad = $("#cantidadProducto").val();
	var precio = $("#precioRango").val();
	
	var valorItbis = 18; //esta cableado en el código original validar
	
	//verificacion para articulos con imei
	if($("#tipoArticulo").val() == "SI"){
		var count = 0;
		var seriales = [];
		//Evento cuando se pasen los seriales a la derecha
		 $('#serialesSeleccionados li').each(function (i) {
	           var index = $(this).index();
	           seriales.push($(this).text());
	           count++;
	       });
		 //Si no selecciona ningun serial mostrar mensaje de alerta
		if(count==0){
			Swal.fire({
				  title: 'Advertencia!',
				  text: 'Debe seleccionar al menos un serial para este articulo',
				  position: 'top',
				  icon: 'warning',
				  confirmButtonText: 'Cool'
			})
		}else{
			//Selecciona al menos un serial
			 $.post("/articulos/ajax/addArticuloConSerial/",
						{
						  idArticulo: idArticulo,
						  cantidad: cantidad,
						  precio: precio,
						  seriales: seriales.toString()
						},
						function(data, status){
							console.log("Articulo agregado a la facturacion");
							//remover la lista de los seriales seleccionados
							$('#serialesSeleccionados li').remove();
							$("#cantidadProducto").val("0");
							$("#precioRango").val("");
							factura_detalle_items();
					});
		}
	}else{
		//verificacion para articulos sin Imei
		var nombre = $("#nombreArticuloBuscado").val();
		var minimo = $("#precioMinimo").val();
		var mayor = $("#precioMayor").val();
		var conItbis = $("#conItbis").val();
		var disponible = $("#disponibleModal").val();
		var maximo =  $("#precioMaximo").val();

		
		  $.post("/articulos/ajax/addArticuloSinSerial/",
			{
			  idArticulo: idArticulo,
			  cantidad: cantidad,
			  precio: precio,
			  conItbis: conItbis,
			  disponible: disponible,
			  maximo: maximo
			},
			function(data, status){
				console.log("Articulo agregado a la facturacion");
				$("#cantidadProducto").val("0");
				$("#precioRango").val("");
				factura_detalle_items();
		});
	}
});

//Carga el detalle de la factura
function factura_detalle_items(){
	$('#cuerpoFactura').load("/articulos/ajax/loadCuerpoFactura/");
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
					factura_detalle_items();
							});  
			  }
			})
	}
}

function modalSerialesDetalleFactura(IdfacturaDetalle){
	$('#serialesAcct').load("/articulos/ajax/obtenerSeriales/"+IdfacturaDetalle);
	$("#serialesAcctModal").modal("show");
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
					factura_detalle_items();
							});  
			  }
			})
		$("#serialesAcctModal").modal("hide");
		factura_detalle_items();
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
	   					factura_detalle_items();
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
			    		   factura_detalle_items();
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
	 $("#cantidadYprecio").load("/articulos/ajax/obtenerInfoDeSeriales/", { 
         'seriales': seriales.toString()
    },function() {
 	  
 });
});













