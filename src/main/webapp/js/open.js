var ctx = '';
$(function() {
	ctx = $('input[name=ctx]').val();

	var matrix = {};
	var matrixNet = {};
	var normal = {};
	var properties = {};
	
	var t = '<table><tbody>';
	t += '<tr><td>01 : <input type="text" value="" /></td></tr>';
	t += '<tr><td>02 : <input type="text" value="" /></td></tr>';
	t += '<tr><td>03 : <input type="text" value="" /></td></tr>';
	t += '<tr><td>04 : <input type="text" value="" /></td></tr>';
	t += '<tr><td>05 : <input type="text" value="" /></td></tr>';
	t += '<tr><td>06 : <input type="text" value="" /></td></tr>';
	t += '<tr><td>07 : <input type="text" value="" /></td></tr>';
	t += '<tr><td>08 : <input type="text" value="" /></td></tr>';
	t += '<tr><td>09 : <input type="text" value="" /></td></tr>';
	t += '<tr><td>10 : <input type="text" value="" /></td></tr>';
	t += '</tbody></table>';
	
	var url4paper = ctx + '/controller?action=getQuestionnaireOpenPaper';
	$.getJSON(url4paper, function(result) {
		
		matrix = result.matrix;
		matrixNet = result.matrixNet;
		normal = result.normal;
		properties = result.optionGroups;
		
		//matrix
		/*
		for(var index = 0; index < matrix.length; index++) {
			var question = matrix[index];
			var title = question.title;
			var table = '<table><tbody>';
			table += '</tbody></table>';
			$('.content').append('<li class="part select" ><h4 class=title ><span class=subject >'+title+'</span></h4>'+t+'</li>');
		}
		*/
		
		//matrix net
		//$('.content').append('<li class="part select" ><h4 class=title ><span class=subject >请选择您所填写的这些人的相关信息 Matrix Net</span></h4><table></table></li>');
		
		//normal
		for(var groupIndex = 0; groupIndex < normal.length; groupIndex++) {
			var group = normal[groupIndex];
			$('.content').append('<div><h3>'+group.title+'</h3></div>');
			for(var index = 0; index < group.questions.length; index++) {
				var question = group.questions[index];
				var table = '<table><tbody>';
				table += question.radio;
				table += '</tbody></table>';
				$('.content').append('<li class="part select" ><h4 class=title ><span class=subject >'+question.content+'</span></h4>'+table+'</li>');
			}
		}
		
		//person properties
		for(var index = 0; index < properties.length; index++) {
			var property = properties[index];
			var property_ = 'property_'+index;
			var table = '<table><tbody>';
			table += property.radio.replace(/property_a/g, property_);
			table += '</tbody></table>';
			$('.content').append('<li class="part select" ><h4 class=title ><span class=subject >'+property.name+'</span></h4>'+table+'</li>');
		}
		
		$('tr:odd').addClass('odd');
		$('tr:even').addClass('even');
		$('li').each(function(i) {
			var id = i + 1;
			$(this).attr('id', 'li'+id);
			//$(this).attr('name', 'li'+id);
		});
	});
	
	//提交按钮
	$('a').bind('click', function() {
		var selectedOk = true;
		var lis = $('li');
		for(var index = 0; index < lis.length; index++) {
			var li = lis[index];
			var id = $(li).attr('id');
			var content = $(li).find('h4 > span').text();
			var radio = $(li).find('table > tbody > tr > td > input:checked');
			if(radio == null || radio.length == 0) {
				selectedOk = false;
				this.href = '#' + id;
				alert('题目："' + content + '"没有填写!');
				break;
			}
		}
		
		if(selectedOk) {
			$('form').attr('method', 'post');
			$('form').attr('action', 'handler.jsp');
			$('form').submit();
		}
	});
});