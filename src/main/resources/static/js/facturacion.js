$('#articulos').load("/articulos/ajax/getAll");

var idArticuloBuscado;

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
			$.get("/articulos/ajax/getArticuloSinserial/" + idArticuloBuscado,
					function(fragment) {
						$('#articuloBuscado').replaceWith(fragment);
						$('#cantidadProducto').prop('max',
								$('#disponibleModal').val());
					});
			//Valores iniciales en 0
			$("#precioRango").val(0);
			$("#cantidadProducto").val(0);
			//Verificamos el tipo del articulo
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
	
	//guardamos el servicio
	  $.post("/articulos/ajax/addService/",
		{
		  descripcion: descripcion,
		  costo: costo,
		  precio: precio,
		  cantidad: cantidad
		},
		function(data, status){
			alert("Data: " + data + "\nStatus: " + status);
		});
	
//	factura_detalle_items();
//	servicio_limpiar();
	
	
});