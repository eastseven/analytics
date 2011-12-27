var prefix_matrix = 'matrix_';
var prefix_matrix_net = 'matrixNet_';
var prefix_matrix_plus = 'matrixPlus_';
var radios = 0;
var peopleAmount = 0;
var columnNum = 10;

var globalArray = [];
var globalCounter = 1;

$(function() {
	var responderId = $('input[name=responderId]').val();
	var version = $('input[name=version]').val();
	var ctx = $('input[name=ctx]').val();
	
	//读取后台问卷数据
	var url4paper = ctx + '/controller?action=getQuestionnairePaper&version='+version+'&responderId='+responderId;
	$.getJSON(url4paper, function(result) {
		var people = result.people;
		peopleAmount = people.length;
		//console.debug(result);
		//1
		var matrix = result.matrix;
		if (matrix.length > 0) {
			
			$.each(matrix, function(i, item) {
				var questionNo = prefix_matrix + item.id;
				//随机排列其他人，把自己过滤掉
				for(var i = 0; i < people.length; i++) {
					var index = Math.floor(Math.random() * people.length);
					var tmp = people[i];
					people[i] = people[index];
					people[index] = tmp;
				}
				var html = '<li class="part select" ><h4 class=title ><span class=subject >' + item.title + '</span></h4>';
				
				var table = '<table class=options><tbody>';
				var tds = '';
				var rownum = (people.length % columnNum  == 0) ? people.length / columnNum : Math.floor(people.length / columnNum) + 1 ;
				var counter = 0;
				$.each(people, function(i, item) {
					i++;
					var td = '<td><input type="checkbox" name='+questionNo+' value='+item.id+' title='+item.id+' onClick=add(this) /><label>' + item.name + '</label></td>';
					if(i % columnNum == 0) {
						tds += td + '</tr>';
					} else if(i == 1 + counter * columnNum) {
						//var odd = (2 * (counter + 1) != i) ? 'odd' : 'even';
						tds += '<tr>' + td;
						counter++;
					} else
						tds += td;
				});
				table += tds + '</tbody></table>';
				html += table + '</li>';
				$('.content').append(html);
			});
		}
		
		//2
		var matrixNet = result.matrixNet;
		if (matrixNet.length > 0) {
			
			var html = '<li class="part select" ><h4 class=title ><span class=subject >请选择您所填写的这些人的相关信息</span></h4>';
			
			var table = '<table class=options>';
			var thead = '<thead><th>姓名</th>';
			var tbody = '<tbody>';
			
			$.each(matrixNet, function(i, item) {
				var id = prefix_matrix_net + item.id;
				thead += '<th>' + ++i + '.' + item.content + '</th>';
			});
			
			var tds = '';
			$.each(people, function(i, item) {
				//var odd = 'odd';
				var tr = '<tr id='+item.id+' style="display:none;"><td>'+item.name+'</td>';
				for(var index = 0; index < matrixNet.length; index++) {
					var options = matrixNet[index];
					tr += '<td id='+item.id+'_'+index+' >'+options.select+'</td>';
				}
				tr += '</tr>';
				tds += tr;
			});
			
			
			thead += '</thead>';
			tbody += '</tbody>';
			table += tds + thead + tbody + '</table>';
			html += table + '</li>';
			$('.content').append(html);
			
		}
		
		//2.1
//		if (matrixNet.length > 0) {
//			var html = '<li class="part select" ><h4 class=title ><span class=subject >请选择您所填写的这些人的相关信息</span></h4>';
//			
//			var table = '<table class=options id=matrixNetPlus>';
//			var thead = '<thead><th>姓名</th>';
//			var tbody = '<tbody>';
//			
//			$.each(people, function(i, item) {
//				var id = prefix_matrix_net + item.id;
//				thead += '<th>' + item.name + '</th>';
//			});
//			
//			var tds = '';
//			for(var rowIndex = 0; rowIndex < people.length; rowIndex++) {
//				var tr = '<tr >';
//				var rowPerson = people[rowIndex];
//				tr += '<td>'+rowPerson.name+'</td>';
//				for(var colIndex = 0; colIndex < people.length; colIndex++) {
//					var colPerson = people[colIndex];
//					var _id = prefix_matrix_plus + rowPerson.id + '_' + colPerson.id;
//					if(rowIndex >= colIndex) {
//						tr += '<td id='+_id+'></td>';
//					} else {
//						tr += '<td><select id="'+_id+'" name="'+_id+'" disabled=disabled>';
//						tr += '<option value=-1>请选择</option>';
//						tr += '<option value=0>1</option>';
//						tr += '<option value=0>2</option>';
//						tr += '<option value=1>3</option>';
//						tr += '<option value=1>4</option>';
//						tr += '<option value=1>5</option>';
//						tr += '</select></td>';
//					}
//				}
//				tr += '</tr>';
//				tds += tr;
//			}
//			
//			thead += '</thead>';
//			tbody += '</tbody>';
//			table += tds + thead + tbody + '</table>';
//			html += table + '</li>';
//			$('.content').append(html);
//			
//		}
		
		//3
		var group = result.group;
		if (group.length > 0) {
			var html = '';
			$.each(group, function(i, item) {
				html += '<div><h3>' + item.title + '</h3></div>';
				var questions = item.questions;
				$.each(questions, function(i, item) {
					html += '<li class="part select" ><h4 class=title ><span class=subject >' + item.content + '</span></h4>';
					var table = '<table class=options>';
					var tbody = '<tbody>';
					var tds = '';
					
					for(var index = 0; index < item.options.length; index++) {
						var option = item.options[index];
						var _id = 'question_' + item.id; 
						tds += '<tr><td><input type=radio name='+_id+' value='+option.key+' />'+option.value+'</td></tr>';
					}
					
					tbody += '</tbody>';
					table += tds + tbody + '</table>';
					html += table + '</li>';
					radios++;
				});
			});
			$('.content').append(html);
		}
		
		//4
		var selfInfo = result.optionGroups;
		if (selfInfo.length > 0) {
			var html = '';
			$.each(selfInfo, function(i, item) {
				html += '<li class="part select" ><h4 class=title ><span class=subject >' + item.name + '</span></h4>';
				var table = '<table class=options>';
				var tbody = '<tbody>';
				var tds = '';
				for(var index = 0; index < item.options.length; index++) {
					var option = item.options[index];
					var checked = '';
					if(option.selected) checked += ' checked=checked readonly=readonly ';
					tds += '<tr><td><input type=radio name=property_'+ i +' value='+option.id+' '+checked+' />' + option.display + '</td></tr>';
				}
				tbody += '</tbody>';
				table += tds + tbody + '</table>';
				html += table + '</li>';
				radios++;
			});
			
			$('.content').append(html);
		}
		
		$('tr:odd').addClass('odd');
		$('tr:even').addClass('even');
		$('li').each(function(i) {
			var id = i + 1;
			$(this).attr('id', 'li'+id);
			$(this).attr('name', 'li'+id);
		});
	});
	
	//提交按钮
	$('#submitBtn').bind('click', function() {
		var lis = $('li');
		var cssSelector = 'table > tbody > tr > td > ';
		var checkbox = cssSelector + ':checkbox';
		var selectTr = 'table > tbody > tr:visible';
		var bln = false;
		
		//逐行检查
		for(var index = 0; index < lis.length; index++) {
			var li = lis[index];
			if($(li).find(checkbox).length > 0) {//1
				if($(li).find(cssSelector + 'input:checked').length == 0) {
					this.href = '#' + $(li).attr('id');
					alert('题目："' + $(li).find('h4 > span').text() + '"没有填写!');
					break;
				}
			} else if($(li).find(selectTr).length > 0 && $(li).find(selectTr + ' > td > select[id=""]').length > 0) {//2
				var options = $(li).find(selectTr).find('option:selected');
				for(var _index = 0; _index < options.length; _index++) {
					var option = options[_index];
					if($(option).val() == -1) {
						this.href = '#' + $(li).attr('id');
						alert('题目："' + $(li).find('h4 > span').text() + '"没有填写!');
						bln = true;
						break;
					}
				}
			}
			
			console.debug($(li).find('table > tbody > tr > td > :radio'));
			
			if(bln) {
				break;
			}
		}
		
		var checkedRadios = 0;
		$(':radio').each(function() {
			if($(this).attr('checked') == 'checked') checkedRadios++;
		});
		
		console.debug('checked radio amount : '+checkedRadios + ', radios : ' + radios);
	});
});

function add(checkbox) {
	var e = $(checkbox);
	var checked = e.attr('checked');
	var responderId = e.attr('title');
	if(checked) {
		//add td
		globalArray.push(responderId);
		$('#'+responderId).show();
	} else {
		//remove td
		var index = jQuery.inArray(responderId, globalArray);
		if(index != -1) globalArray.splice(index, 1);
		
		var amount = $.grep(globalArray, function(n,i) {return n == responderId;});
		if(amount == 0) {
			$('#'+responderId).hide();
			
			$('select[id*='+responderId+']').each(function() {
				var select = $(this);
				if(select.attr('disabled') === undefined) {
					select.children('option').first().attr('selected', 'selected');
					select.attr('disabled', 'disabled');
					select.parent().css("border","");
				}
			});
		}
	}
	
	//console.debug(globalArray);
	
	if(globalArray.length >= 2) {
		for(var rowIndex = 0; rowIndex < globalArray.length; rowIndex++) {
			var row = globalArray[rowIndex];
			for(var colIndex = 0; colIndex < globalArray.length; colIndex++) {
				var col = globalArray[colIndex];
				var _id = prefix_matrix_plus + row + '_' + col;
				var select = $('#'+_id);
				if(select && $(select).children('option').length > 0) {
					select.removeAttr('disabled');
					select.parent().css("border","1px solid red");
				}
			}
		}
	}
}