<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>调查问卷</title>

<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>

<link rel="stylesheet" href="css/colorbox.css" />
<script type="text/javascript" src="js/jquery.colorbox-min.js"></script>

<link rel="stylesheet" href="js/jquery/css/smoothness/jquery-ui-1.8.16.custom.css" />
<script type="text/javascript" src="js/jquery/js/jquery-ui-1.8.16.custom.min.js"></script>

</head>
<body>

	<script type="text/javascript">
		var responderId = <%=request.getParameter("id") %>
		var version = <%=request.getParameter("v") %>;
		var prefix_matrix = 'matrix_';
		var prefix_matrix_net = 'matrixNet_';
		var globalArray = [];
		var radios = 0;
		var peopleAmount = 0;
		$(function() {
			var url4paper = 'controller?action=getQuestionnairePaper&version='+version+'&responderId='+responderId;
			
			$.getJSON(url4paper, function(result) {
				console.debug(result);

				var titleNo = 1;
				var people = result.people;
				peopleAmount = people.length;
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
						var html = '';
						$.each(people, function(i, item) {
							html += '<input type="checkbox" name='+questionNo+' value='+item.id+' title='+item.id+' onClick=add(this) />' + item.name + '  ';
						});
						$('#matrix').append('<div><p>' + titleNo++ + '.' + item.title + '</p>'+html+'</div>');
						radios++;
					});
				}

				//2
				var matrixNet = result.matrixNet;
				if (matrixNet.length > 0) {
					$('#matrixNet').before(titleNo + '.请选择您所填写的这些人的相关信息<br/>');
					var th = '';
					$.each(matrixNet, function(i, item) {
						var id = prefix_matrix_net + item.id;
						th += '<th>' + ++i + '.' + item.content + '</th>';
					});
					$('#matrixNetTh').after(th);
					
					var html = '';
					$.each(people, function(i, item) {
						var tr = '<tr id='+item.id+' style="display:none;"><td>'+item.name+'</td>';
						for(var index = 0; index < matrixNet.length; index++) {
							var options = matrixNet[index];
							tr += '<td id='+item.id+'_'+index+' >'+options.select+'</td>';
						}
						tr += '</tr>';
						html += tr;
					});
					$('#matrixNetTBody').append(html);
					
					titleNo++;
				}

				//3
				var group = result.group;
				if (group.length > 0) {
					var html = '';
					$.each(group, function(i, item) {
						html += '<div><p>' + titleNo + '.' + item.title + '</p>';
						var questions = item.questions;
						$.each(questions, function(i, item) {
							html += '<p>' + titleNo + '.' + ++i + item.content +'</p>';
							html += '<p>' + item.radio + '</p>';
							radios++;
						});
						html += '</div>';
						titleNo++;
					});
					$('#normalQuestion').append(html);
				}

				//4
				var selfInfo = result.optionGroups;
				if (selfInfo.length > 0) {
					var html = '';
					$.each(selfInfo, function(i, item) {
						html += '<div>';
						html += '<p>' + titleNo++ + '.' + item.name + '</p>';
						html += '<p>';
						for(var index = 0; index < item.options.length; index++) {
							var option = item.options[index];
							var checked = '';
							if(option.selected) checked += ' checked=checked ';
							html += '<input type=radio name=property_'+ i +' value='+option.id+' '+checked+' />' + option.display + '  ';
						}
						html +='</p>';
						html += '</div>';
						radios++;
					});
					
					$('#selfInfo').append(html);
				}
			});

			$('#questionnaire').wrap('<form id=questionnaireForm method="post" action=""></form>');
			
			//form submit event
			$('#submitBtn').bind('click', function() {
				$('#questionnaireForm').attr('action', 'handler.jsp');

				//移出display为none的tr中的下拉框的name属性
				$.each($('tr:hidden > td > select'), function(i, item) {
					$(this).removeAttr('name');
				});
				var selectedOk = true;
				$.each($('tr:visible > td'), function(i, item) {
					var peopleId = $(this).attr('id');
					if(peopleId != undefined) {
						var selected = $(this).children('select');
						selected = $(selected[0]).children('option[selected]');
						var value = $(selected).attr('value');
						//console.debug(value);
						if(value == -1) {
							selectedOk = selectedOk & false;
						}
						value = value + '_' + peopleId;
						selected.attr('value', value);
						//console.debug(selected);
					}
				});
				var checkedRadios = 0;//$('input[type=radio][checked]').length;
				$(':radio').each(function() {
					//console.debug($(this).attr('checked'));
					if($(this).attr('checked') == 'checked') checkedRadios++;
				});
				
				$('#matrix').children('div').each(function(i, item) {
					var checked = $(item).children(':checkbox[checked]').length;
					console.debug(checked);
					if(checked >= 1) checkedRadios++;
				});
				
				console.debug('checked radio amount : '+checkedRadios + ', radios : ' + radios);
				
				//TODO validate
				if(checkedRadios == radios && selectedOk) {
					$('#questionnaireForm').submit();
				} else {
					alert('问卷未完成，请将所有题目做完，谢谢合作');
				}
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
				if(amount == 0) $('#'+responderId).hide();
			}
			console.debug(globalArray);
		}
	</script>

	<div id="questionnaire">
		<input type="hidden" name="responderId" value="<%=request.getParameter("id") %>"/>
		<input type="hidden" name="version" value="<%=request.getParameter("v") %>" />
		<div id="matrix"></div>
		<div id="matrixNet">
			<table id="matrixNetTable" border="1">
				<thead id="matrixNetTHead">
					<tr id="matrixNetTr">
						<th id="matrixNetTh">姓名</th>
					</tr>
				</thead>
				<tbody id="matrixNetTBody" ></tbody>
			</table>
		</div>
		<div id="normalQuestion"></div>
		<div id="selfInfo"></div>
		
		<!-- <input type="checkbox" title="aaa"> -->
	</div>
	<button id="submitBtn">提交</button>
</body>
</html>