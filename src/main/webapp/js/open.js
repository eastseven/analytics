var ctx = '';

var prefix_matrix = 'matrix_';
var prefix_matrix_net = 'matrixNet_';
var prefix_matrix_plus = 'matrixPlus_';

var matrixNetTr = '';

$(function() {
	ctx = $('input[name=ctx]').val();

	var matrix = {};
	var matrixNet = {};
	var normal = {};
	var properties = {};
	
	var url4paper = ctx + '/controller?action=getQuestionnaireOpenPaper';
	$.getJSON(url4paper, function(result) {
		
		matrix = result.matrix;
		matrixNet = result.matrixNet;
		normal = result.normal;
		properties = result.optionGroups;
		
		//TODO matrix
		for(var index = 0; index < matrix.length; index++) {
			
			var question = matrix[index];
			var title = question.title;
			var questionNo = prefix_matrix + question.id;

			var table = '<table><tbody>';
			table += '<tr><td>01 : <input type="text" name="xname" value="" onchange="add(this)" id="'+questionNo+'_0"/></td></tr>';
			table += '<tr><td>02 : <input type="text" name="xname" value="" onchange="add(this)" id="'+questionNo+'_1"/></td></tr>';
			table += '<tr><td>03 : <input type="text" name="xname" value="" onchange="add(this)" id="'+questionNo+'_2"/></td></tr>';
			table += '<tr><td>04 : <input type="text" name="xname" value="" onchange="add(this)" id="'+questionNo+'_3"/></td></tr>';
			table += '<tr><td>05 : <input type="text" name="xname" value="" onchange="add(this)" id="'+questionNo+'_4"/></td></tr>';
			table += '<tr><td>06 : <input type="text" name="xname" value="" onchange="add(this)" id="'+questionNo+'_5"/></td></tr>';
			table += '<tr><td>07 : <input type="text" name="xname" value="" onchange="add(this)" id="'+questionNo+'_6"/></td></tr>';
			table += '<tr><td>08 : <input type="text" name="xname" value="" onchange="add(this)" id="'+questionNo+'_7"/></td></tr>';
			table += '<tr><td>09 : <input type="text" name="xname" value="" onchange="add(this)" id="'+questionNo+'_8"/></td></tr>';
			table += '<tr><td>10 : <input type="text" name="xname" value="" onchange="add(this)" id="'+questionNo+'_9"/></td></tr>';
			table += '</tbody></table>';
			
			table = table.replace(/xname/g, questionNo);
			
			$('.content').append('<li class="part select" ><h4 class=title ><span class=subject >'+title+'</span></h4>'+table+'</li>');
		}
		
		//matrix net
		var matrixNetTable = '<table id="matrixNet"><thead><tr><td>姓名</td>';
		for(var index = 0; index < matrixNet.length; index++) {
			var net = matrixNet[index].questions[0];
			matrixNetTable += '<td>'+net.content+'</td>';
			matrixNetTr += '<td>'+net.select+'</td>';
		}
		matrixNetTable += '</tr></thead><tbody id="matrixNetTbody"></tbody></table>';
		$('.content').append('<li class="part select" ><h4 class=title ><span class=subject >请选择您所填写的这些人的相关信息 Matrix Net</span></h4>'+matrixNetTable+'</li>');
		
		//matrix plus
		$('.content').append('<li class="part select" ><h4 class=title ><span class=subject >请选择您所填写的这些人的相关信息 Matrix Plus</span></h4><table></table></li>');
				
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

function add(input) {
	var id = 'netTr_' + $(input).attr('id');
	var tdId = id + '_td';
	var value = $(input).val();
	
	console.debug($(input));
	if($('#'+id).length == 0) {
		$('#matrixNetTbody').append('<tr id="'+id+'"><td id="'+tdId+'">'+value+'</td>'+matrixNetTr+'</tr>');
	} else {
		var origin = $('#'+tdId).text();
		console.debug('当前值：'+value + ',原始值：' + origin);
		if(value == '') $('#'+id).remove();
		else if(value != origin) $('#'+tdId).text(value);
	}
	
}