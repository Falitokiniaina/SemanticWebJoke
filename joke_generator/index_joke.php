<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>joke generating</title>
<link rel="stylesheet" href="lib/css/index.css">
<link rel="stylesheet" href="lib/css/jquery-ui.css">
<script src="lib/js/jquery-1.10.2.js"></script>
<script src="lib/js/jquery-ui.js"></script>
  <script>
(function($){
	clk_bt_read = function(){		
		clk_bt_stop_read();
		var msg = new SpeechSynthesisUtterance();
		var voices = window.speechSynthesis.getVoices();
		msg.voice = voices[$("#slc_num_voice").val()]; // Note: some voices don't support altering params
		msg.text = $("#txt_joke").val();
		msg.lang = 'en-US';
	    window.speechSynthesis.speak(msg);		
	};
	clk_generate = function(){
			clk_bt_stop_read();
			if($("#in_joke_about").val() == ""){
				alert('Enter any topic of joke.');
				$("#in_joke_about").focus();
			}
			else{
				$.ajax({
					async:false,
					url:"generate_joke.php?topic="+$("#in_joke_about").val(),
					success : function(html){
								$("#txt_joke").val(html);
								if($('#chbx_auto_read').is(':checked') && $("#txt_joke").val().length <= 350)
									clk_bt_read();
					},
				});
			}
	};
	key_up_joke = function(e){
					if(e.keyCode == 13){
						$("#bt_generate").focus();
						clk_generate();
					}
	};	
	clk_bt_stop_read = function(){
			 window.speechSynthesis.cancel();
	};
})(jQuery)  
  	$(document).ready(function(e) {
		$( "#in_joke_about" ).autocomplete({						
			source: 'get_category.php',
			max:3
		});  
		$("#in_joke_about").keyup(key_up_joke);				
		$("#bt_generate").click(clk_generate);		
		$("#bt_stop_read").click(clk_bt_stop_read);
		$("#bt_read").click(clk_bt_read);		
	});
  </script>
</head>

<body>
<div class="div_vertical_bar"></div>
<div class="div_tittle">
	<label id="lb_title">JOKE GENERATOR</label>
</div>
<div class="div_content ui-widget">
	<fieldset id="fl_content">
    	<label for="in_joke_about">Joke about : </label><input type=text placeholder="Joke about ..." name="in_joke_about" id="in_joke_about" />
        <input type="button" value="Generate" name="bt_generate" id="bt_generate"/>
        <input type="button" value="Read again" name="bt_read" id="bt_read"/>
        <br /><br />
        <textarea cols="75" rows="10" id="txt_joke"></textarea><br />
        <input type="checkbox" name="chbx_auto_read" id="chbx_auto_read" checked=checked /><label for="chbx_auto_read">Auto read</label>
        <select name="slc_num_voice" id="slc_num_voice">
        	<option value="1">Man</option>
            <option value="0">Woman 1</option>
            <option value="2">Woman 2</option>
            <option value="3">Woman 3</option>            
        </select>
        <input type="button" value="Stop reading" name="bt_stop_read" id="bt_stop_read" />
    </fieldset>
</div>
</html>