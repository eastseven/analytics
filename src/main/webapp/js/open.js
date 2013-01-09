var ctx = '';

var prefix_matrix = 'matrix_';
var prefix_matrix_net = 'matrixNet_';
var prefix_matrix_plus = 'matrixPlus_';

$(function() {
	ctx = $('input[name=ctx]').val();
	
	$('a[name=submit]').hide();
	
	var matrix = {};
	var matrixNet = {};
	var normal = {};
	var properties = {};
	
	var url4paper = ctx + '/controller?action=getQuestionnaireOpenPaper';
	$.getJSON(url4paper, function(result) {
		$('#questionnaire').data('result', result);
		
		loadMatrix(result);
		
	});
	
	//提交按钮
	$('a[name=submit]').bind('click', function() {
		var selectedOk = true;

		var select = $('option:selected');
		for(var index = 0; index < select.length; index++) {
			var option = select[index];
			if($(option).val() == -2) {
				//li2
				var content = $('#li3').find('h4 > span').text();
				selectedOk = false;
				this.href = '#li3';
				alert('题目："3.' + content + '"没有填写!');
				break;
			} else if($(option).val() == -1) {
				//li3
				var content = $('#li4').find('h4 > span').text();
				selectedOk = false;
				this.href = '#li4';
				alert('题目："4.' + content + '"没有填写!');
				break;
			}
			
			if(!selectedOk) break;
		}
		
		if(selectedOk) {
			var lis = $('li');
			for(var index = 0; index < lis.length; index++) {
				if(index > 3) {
					var li = lis[index];
					var id = $(li).attr('id');
					var content = $(li).find('h4 > span').text();
					var radio = $(li).find('table > tbody > tr > td > input:checked');
					if(radio == null || radio.length == 0) {
						selectedOk = false;
						this.href = '#' + id;
						var idString = ''+id;
						alert('题目："'+idString.replace(/li/, '')+'.' + content + '"没有填写!');
						break;
					}
				}
			}
		}
		//console.debug(selectedOk);
		if(selectedOk) {
			$('#info').wrap('<form></form>');
			$('form').attr('method', 'post');
			$('form').attr('action', 'handler.jsp');
			$('form').submit();
		}
	});
	
});

function loadMatrix(result) {
	matrix = result.matrix;
	for(var index = 0; index < matrix.length; index++) {
		
		var question = matrix[index];
		var title = question.title;
		var questionNo = 'person' ;//+ question.id;

		var table = '<table><tbody>';
		table += '<tr><td>第一人 : <input type="text"  value=""  id="'+questionNo+'0" name="'+questionNo+'0"/></td></tr>';
		table += '<tr><td>第二人 : <input type="text"  value=""  id="'+questionNo+'1" name="'+questionNo+'1"/></td></tr>';
		table += '<tr><td>第三人 : <input type="text"  value=""  id="'+questionNo+'2" name="'+questionNo+'2"/></td></tr>';
		table += '<tr><td>第四人 : <input type="text"  value=""  id="'+questionNo+'3" name="'+questionNo+'3"/></td></tr>';
		table += '<tr><td>第五人 : <input type="text"  value=""  id="'+questionNo+'4" name="'+questionNo+'4"/></td></tr>';
		table += '<tr><td>第六人 : <input type="text"  value=""  id="'+questionNo+'5" name="'+questionNo+'5"/></td></tr>';
		table += '<tr><td>第七人 : <input type="text"  value=""  id="'+questionNo+'6" name="'+questionNo+'6"/></td></tr>';
		table += '<tr><td>第八人 : <input type="text"  value=""  id="'+questionNo+'7" name="'+questionNo+'7"/></td></tr>';
		table += '<tr><td>第九人 : <input type="text"  value=""  id="'+questionNo+'8" name="'+questionNo+'8"/></td></tr>';
		table += '<tr><td>第十人 : <input type="text"  value=""  id="'+questionNo+'9" name="'+questionNo+'9"/></td></tr>';
		
		table += '<tr><td align="center" id="button">';
		table += '<a style="cursor: pointer;"            onclick="nextpage()" name="nextpage"><button class="subutton">下一页</button></a>';
		table += '<a style="cursor: pointer;display: none;" onclick="reset()"    name="reset"><button class="subutton">重  填</button></a>';
		table += '</td></tr>';
		
		table += '</tbody></table>';
		
		//table = table.replace(/xname/g, questionNo);
		
		$('.content').append('<li class="part select" ><h4 class=title ><span class=subject >'+title+'</span></h4>'+table+'</li>');
	}
}

var matrixNetTr = '';
function loadMatrixNet(result) {
	matrixNet = result.matrixNet;
	//matrix net
	matrixNetTr = '';
	var matrixNetTable = '<table id="matrixNet" border=1><thead><tr><td align="right">姓名</td>';
	for(var index = 0; index < matrixNet.length; index++) {
		var net = matrixNet[index].questions[0];
		matrixNetTable += '<td align="center">'+net.content+'</td>';
		matrixNetTr += '<td>'+net.select+'</td>';
	}
	
	matrixNetTable += '</tr></thead><tbody id="matrixNetTbody">';
	matrixNetTable += '</tbody></table>';
	
	//matrix trapezoid
	var trapezoid = '<table id="trapezoid" border=1><tbody>';
	//load people
	var people = $(':text[value!=""]');
	var rownum = people.length + 1;
	var colnum = people.length + 1;
	for(var row = 0; row < rownum; row++) {
		trapezoid += '<tr>';
		
		for(var col = 0; col < colnum; col++) {
			
			if(row == 0) {
				if(col == 0) trapezoid += '<td />';
				else {
					var name = $(people[col-1]).val();
					var peopleId = $(people[col-1]).attr('id');
					trapezoid += '<td align="center">'+name+'</td>';
				}
			} else {
				if(col == 0) {
					var name = $(people[row-1]).val();
					trapezoid += '<td align="right">'+name+'</td>';
				}else if(row <= col) {
					trapezoid += '<td />';
				} else {
					//[row,column]
					var rId = $(people[row-1]).attr('id');
					var cId = $(people[col-1]).attr('id');
					var _id = prefix_matrix_plus + rId + '_' + cId;
					trapezoid += '<td align="center"><select name='+_id+'>';
					trapezoid += '<option value=-2>请选择</option>';
					trapezoid += '<option value=0>完全不认识</option>';
					trapezoid += '<option value=0>不熟悉</option>';
					trapezoid += '<option value=1>一般</option>';
					trapezoid += '<option value=1>比较熟悉</option>';
					trapezoid += '<option value=1>非常熟悉</option>';
					trapezoid += '</select></td>';
				}
			}
			
		}
		
		trapezoid += '</tr>';
	}
	trapezoid += '</tbody></table>';
	
	$('.content').append('<li class="part select" ><h4 class=title ><span class=subject >(本题为示例，无须作答)请选择您所填写的这些人的相互熟悉程度</span></h4>'+sampleTable+'</li>');
	$('.content').append('<li class="part select" ><h4 class=title ><span class=subject >请选择您所填写的这些人的相互熟悉程度</span></h4>'+trapezoid+'</li>');
	$('.content').append('<li class="part select" ><h4 class=title ><span class=subject >请选择您所填写的这些人的相关信息</span></h4>'+matrixNetTable+'</li>');
	
}

function loadNormal(result) {
	normal = result.normal;
	properties = result.optionGroups;
	
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
	$('.content').append('<div><h3>个人相关信息</h3></div>');
	for(var index = 0; index < properties.length; index++) {
		var property = properties[index];
		var property_ = 'property_'+index;
		var table = '<table><tbody>';
		table += property.radio.replace(/property_a/g, property_);
		table += '</tbody></table>';
		$('.content').append('<li class="part select" ><h4 class=title ><span class=subject >'+property.name+'</span></h4>'+table+'</li>');
	}
	
	$('li').each(function(i) {
		var id = i + 1;
		$(this).attr('id', 'li'+id);
		$(this).attr('name', 'li_normal');
	});
}

// ----- Event ----- //

function nextpage() {
	var count = 0;
	$(':text').each(function() {
		if($(this).val() != '') count++;
	});
	
	if(count <= 1) {
		alert('请填写至少两个你熟悉的人的名字');
	} else {
		var result = $('#questionnaire').data('result');
		
		$('a[name=nextpage]').hide();
		$('a[name=reset]').show();
		loadMatrixNet(result);
		loadNormal(result);
		
		//load people
		$(':text[value!=""]').each(function() {
			add(this);
		});
		
		$('a[name=submit]').show();
	}
}

function reset() {
	var index = 0;
	$('.content').children().each(function() {
		if(index != 0) $(this).remove();
		index++;
	});
	
	$(':text').each(function() {
		$(this).val('');
	});
	
	$('a[name=nextpage]').show();
	$('a[name=reset]').hide();
	$('a[name=submit]').hide();
	$('#matrixNetTbody').empty();
}

function add(input) {
	var id = 'netTr_' + $(input).attr('id');
	var tdId = id + '_td';
	var value = $(input).val();
	
	if($('#'+id).length == 0) {
		var _matrixNetTr = matrixNetTr.replace(/personid/g, $(input).attr('id'));
		$('#matrixNetTbody').append('<tr id="'+id+'"><td id="'+tdId+'" align="right">'+value+'</td>'+_matrixNetTr+'</tr>');
	} else {
		var origin = $('#'+tdId).text();
		//console.debug('当前值：'+value + ',原始值：' + origin);
		if(value == '') $('#'+id).remove();
		else if(value != origin) $('#'+tdId).text(value);
	}
	
}

function del(input) {
	//console.debug($(input).parent().parent());
	$(input).parent().parent().remove();
}