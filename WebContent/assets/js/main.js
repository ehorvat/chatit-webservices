jQuery(function($) {

	$("#changeForm").validate({
		onkeyup : true,
		rules : {
			new_password : {
				required : true
			},
			conf_password : {
				required : true
			}
		},
		errorPlacement : function() {
			return false;
		}

	});
	
	$("#changeForm").submit(function( event ) {
		var v1 = $.trim($("#pass").val());
		var v2 = $.trim($("#confPass").val());
		
		if(v1 != v2){
			alert("passwords don't match");
			event.preventDefault();

		}
	});

});