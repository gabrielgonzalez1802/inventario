var idTaller = $("#idTaller").val();
$('#articulos').load("/talleres/ajax/getListaArticulos/"+idTaller,function(){});
var seleccionado = $("#articulos").select2();
//Cargamos los detalles
taller_detalle_items();

$("#btnAddDiagnosticoModal").click(function(e){
	e.preventDefault();
	$("#diagnosticoModal").modal("show");
});

$("#btnAddDiagnostico").click(function(e){
	e.preventDefault();
	var nombre = $("#nombreDiagnostico").val();
	var costo = $("#costoDiagnostico").val();
	var precio = $("#precioDiagnostico").val();
	var idTaller = $("#idTaller").val();
	
	if(!nombre || !costo || !precio){
		Swal.fire({
			title : 'Advertencia!',
			text : 'Todos los campos son requeridos',
			position : 'top',
			icon : 'warning',
			confirmButtonText : 'Cool'
		})
	}else{
		var idArticulo = $("#idArticulo").val();
		 $.post("/talleres/ajax/addDetail/", {
			 'idTaller' : idTaller,
			 'nombre' : nombre,
			 'costo' : costo,
			 'cantidad' : 1,
			 'precio' : precio,
			 'idArticulo' : 0,
			 'tallerArticuloId' : 0
		 },function(data){
			taller_detalle_items();
			$("#diagnosticoModal").modal("hide");
		 });
	}
});

$("#btnLiberarEquipo").click(function(e){
	e.preventDefault();
	var idTaller = $("#idTaller").val();
	Swal.fire({
		  title: 'Esta seguro?',
		  text: "Desea liberar el equipo?",
		  icon: 'warning',
		  position: 'top',
		  showCancelButton: true,
		  confirmButtonColor: '#3085d6',
		  cancelButtonColor: '#d33',
		  confirmButtonText: 'Si, Liberar!'
		}).then((result) => {
		  if (result.value) {
			  $.post("/talleres/ajax/liberarEquipo/", {
					 'idTaller' : idTaller
				 },function(data){
					 $('#responseLiberado').replaceWith(data);
					 if($('#responseLiberado').val()=="1"){
						 Swal.fire({
							  title: 'Muy bien!',
							  text: 'EL equipo ha sido liberado.',
							  icon: 'success',
							  position: 'top',
							  confirmButtonText: 'Cool'
						  })
						 setInterval(function(){
							 window.location = '/talleres/'; 
						    },2000);
					 }else{
						 Swal.fire({
							  title: 'Error!',
							  text: 'EL equipo no ha sido liberado. Intente mas tarde',
							  icon: 'error',
							  position: 'top',
							  confirmButtonText: 'Cool'
						  })
					 }
				 });
		  }
		})
});

$("#estadoTaller").change(function(e){
	e.preventDefault();
	var idTaller = $("#idTaller").val();
	var estado = $("#estadoTaller").val();
	if(estado == 'Devolver'){
		//Verificamos que no existan registros
		var nFilas = $("#cuerpoTaller tr").length;
	    var nColumnas = $("#cuerpoTaller tr:last td").length;
	    if(nFilas > 2){
	    	Swal.fire({
				  title: 'Advertencia!',
				  text: 'Para devolver al cliente no puede tener nada agregado!!',
				  icon: 'warning',
				  position: 'top',
				  confirmButtonText: 'Cool'
			  })
			  $("#estadoTaller").val("Abierto");
	    }else{
	    	$.post("/talleres/ajax/updateEstado/", {
	   		 'idTaller' : idTaller,
	   		 'estado' : estado
	   	 },function(data){
	   		$('#responseUpdateEstado').replaceWith(data);
	   		if($('#responseUpdateEstado').val()==0){
	   			Swal.fire({
					title : 'Advertencia!',
					text : 'Este taller esta relacionado a una factura, no se puede cambiar el estado',
					position : 'top',
					icon : 'warning',
					confirmButtonText : 'Cool'
				})
	   		}
	   	 });
	    }
	}else{
		$.post("/talleres/ajax/updateEstado/", {
	   		 'idTaller' : idTaller,
	   		 'estado' : estado
	   	 },function(data){
	   		$('#responseUpdateEstado').replaceWith(data);
	   		if($('#responseUpdateEstado').val()==0){
	   			Swal.fire({
					title : 'Advertencia!',
					text : 'Este taller esta relacionado a una factura, no se puede cambiar el estado',
					position : 'top',
					icon : 'warning',
					confirmButtonText : 'Cool'
				})
	   		}
	   	 });
	}
});

//Carga el detalle del taller
function taller_detalle_items(){
	var idTaller = $("#idTaller").val();
	$('#cuerpoTaller').load("/talleres/ajax/loadCuerpoTaller/"+idTaller,function(){});
}

$("#btnRegresar").click(function(e){
	e.preventDefault();
	window.location = '/talleres/'; 
});

//Evento cuando se seleccine un articulo
$('#articulos').on("select2:select", function(e) { 
	if(seleccionado.val()!=""){
		var idTallerArticulo = seleccionado.val();
		var valor = $('#articulos :selected').text();
		if(valor!=""){
			$('#infoSelection').load("/tallerArticulos/ajax/getInfo/"+idTallerArticulo,function(data){
				$('#cantidad').prop('max', $('#disponible').val());
				$("#cantidad").val("1");
				$("#precio").val($("#precioArticulo").val());
			});
		}
	}else{
		$("#cantidad").val("");
		$("#precio").val("");
	}
});

$("#agregarItem").click(function(e){
	e.preventDefault();
	if(seleccionado.val()==""){
		Swal.fire({
			title : 'Advertencia!',
			text : 'El articulo no puede estar vacio',
			position : 'top',
			icon : 'warning',
			confirmButtonText : 'Cool'
		})
		$("#cantidad").val("");
		$("#precio").val("");
	}else{
		var idTaller = $("#idTaller").val();
		var nombre = $('#articulos :selected').text();
		var costo = $("#costoArticulo").val();
		var cantidad = $("#cantidad").val();
		var precio = $("#precio").val();
		var idArticulo = $("#idArticulo").val();
		var tallerArticuloId = seleccionado.val()
		$.post("/talleres/ajax/addDetail/", {
			 'idTaller' : idTaller,
			 'nombre' : nombre,
			 'costo' : costo,
			 'cantidad' : cantidad,
			 'precio' : precio,
			 'idArticulo' : idArticulo,
			 'tallerArticuloId' : tallerArticuloId
		 },function(data){
			 $('#responseAddDetail').replaceWith(data);
			 if( $('#responseAddDetail').val()==0){
					//recargar la lista de articulos
					$('#articulos').load("/talleres/ajax/getListaArticulos/"+idTaller,function(){});
					$("#cantidad").val("");
					$("#precio").val("");
					focusSelectArticuloNew();
					taller_detalle_items();
			 }else{
				 Swal.fire({
						title : 'Advertencia!',
						text : 'El precio del articulo no puede ser menor al precio x mayor',
						position : 'top',
						icon : 'warning',
						confirmButtonText : 'Cool'
					})
			 }
		 });
	}
});

function focusSelectArticuloNew(){
	$('#articulos').val('').trigger('change.select2');
	$('#articulos').select2('focus');
//	$('#articulos').select2('open');
}

function eliminarItem(idItem){
	$.post("/talleres/ajax/deleteDetail/", {
		 'idDetalle' : idItem
	 },function(data){
		$('#responseDeleteDetail').replaceWith(data);
		if($("#responseDeleteDetail").val() == 0){
			Swal.fire({
				title : 'Advertencia!',
				text : 'Este taller esta relacionado a una factura, para eliminar los items debe cancelar la factura pendiente',
				position : 'top',
				icon : 'warning',
				confirmButtonText : 'Cool'
			})
		}else{
			//recargar la lista de articulos
			$('#articulos').load("/talleres/ajax/getListaArticulos/"+idTaller,function(){});
			$("#cantidad").val("");
			$("#precio").val("");
			focusSelectArticuloNew();
			taller_detalle_items();
			focusSelectArticuloNew();
		}
	 });
}
