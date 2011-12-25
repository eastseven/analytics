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

<style type="text/css">
	.separtor div { border-top: #cccccc 2px silver; padding: 20px; }
	.separtor h1 { font-size: 16px; font-weight: bold; }
	.separtor p { white-space:2em; }
</style>

<link rel="stylesheet" href="./a_files/default.css" type="text/css">
<link rel="stylesheet" href="./a_files/default(1).css" type="text/css">
</head>
<body>

	<script type="text/javascript" >
	var responderId = <%=request.getParameter("id") %>
	var version = <%=request.getParameter("v") %>;
	var prefix_matrix = 'matrix_';
	var prefix_matrix_net = 'matrixNet_';
	var prefix_matrix_plus = 'matrixPlus_';
	var globalArray = [];
	var radios = 0;
	var peopleAmount = 0;
	$(function() {
		var url4paper = 'controller?action=getQuestionnairePaper&version='+version+'&responderId='+responderId;
		
		$.getJSON(url4paper, function(result) {
			//console.debug(result);

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
					$('#matrix').append('<div class=separtor ><h1 class=separtor>' + titleNo++ + '.' + item.title + '</h1><p class=separtor>'+html+'</p></div>');
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

			//2.1
			if (matrixNet.length > 0) {
				$('#matrixNetPlus').before(titleNo + '.请选择您所填写的这些人的相关信息<br/>');
				
				var th = '';
				$.each(people, function(i, item) {
					var id = prefix_matrix_net + item.id;
					th += '<th>' + item.name + '</th>';
				});
				$('#matrixNetPlusTh').after(th);
				
				var html = '';
				for(var rowIndex = 0; rowIndex < people.length; rowIndex++) {
					var tr = '<tr>';
					var rowPerson = people[rowIndex];
					tr += '<td>'+rowPerson.name+'</td>';
					for(var colIndex = 0; colIndex < people.length; colIndex++) {
						var colPerson = people[colIndex];
						var _id = prefix_matrix_plus + rowPerson.id + '_' + colPerson.id;
						if(rowIndex >= colIndex) {
							tr += '<td id='+_id+'></td>';
						} else {
							tr += '<td><select id="'+_id+'" name="'+_id+'" disabled=disabled>';
							tr += '<option value=-1>请选择</option>';
							tr += '<option value=0>1</option>';
							tr += '<option value=0>2</option>';
							tr += '<option value=1>3</option>';
							tr += '<option value=1>4</option>';
							tr += '<option value=1>5</option>';
							tr += '</select></td>';
						}
					}
					tr += '</tr>';
					html += tr;
				}
				$('#matrixNetPlusTBody').append(html);
				
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
						html += '<p class=separtor >' + titleNo + '.' + ++i + item.content +'</p>';
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
					html += '<div class=separtor >';
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
			
			$.each($('select[id^='+prefix_matrix_plus+']'), function(i, item) {
				var selected = $(this).children('option[selected]');
				var value = $(selected).attr('value');
				if($(this).attr('disabled') != 'disabled' && value == -1) {
					//console.debug($(this));
					selectedOk = selectedOk & false;
				}
			});
			console.debug(selectedOk);
			
			var checkedRadios = 0;
			$(':radio').each(function() {
				//console.debug($(this).attr('checked'));
				if($(this).attr('checked') == 'checked') checkedRadios++;
			});
			
			$('#matrix').children('div').each(function(i, item) {
				var checked = $(item).children(':checkbox[checked]').length;
				//console.debug(checked);
				if(checked >= 1) checkedRadios++;
			});
			
			console.debug('checked radio amount : '+checkedRadios + ', radios : ' + radios);
			
			//TODO validate
			if(checkedRadios == radios && selectedOk) {
				$('#questionnaireForm').submit();
			} else {
				//$('#questionnaireForm').submit();
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
			if(amount == 0) {
				$('#'+responderId).hide();
				
				$('select[id*='+responderId+']').each(function() {
					var select = $(this);
					if(select.attr('disabled') === undefined) {
						select.children('option').first().attr('selected', 'selected');
						select.attr('disabled', 'disabled');
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
					if(select) select.removeAttr('disabled');
				}
			}
		}
	}
	</script>

	<div id="main">
		<form method="post" id="survey">
				<div class="survey_body">
					<link rel="stylesheet" href="./a_files/default(1).css" type="text/css">
					<div class="page" _sn="7afe655f-bf5f-4a90-b4c2-193ad6e42e9a">
						<div class="page-header">
							<h1 class="survey-title">[新建调查表]</h1>
						</div>
						<ol class="content">
							
							<li class="part select" _require="1"
								_sn="88850302-4534-4dce-902d-73e0990b4e68">
								<h4 class="title">
									<span class="subject">您的问题是</span> <span class="require">*</span>
									<label class="error"></label>
								</h4>
								<table class="options">
									<tbody>
										<tr class="odd">
											<td><input type="radio"
												id="f0bf5c05-e4db-4ff8-95a0-0ab1e97c7790"
												name="88850302-4534-4dce-902d-73e0990b4e68[]"
												value="f0bf5c05-e4db-4ff8-95a0-0ab1e97c7790"><label
												for="f0bf5c05-e4db-4ff8-95a0-0ab1e97c7790">A. BUG</label></td>
											<td><input type="radio"
												id="21cdac11-f92a-4b10-96d6-fbf44b414f07"
												name="88850302-4534-4dce-902d-73e0990b4e68[]"
												value="21cdac11-f92a-4b10-96d6-fbf44b414f07"><label
												for="21cdac11-f92a-4b10-96d6-fbf44b414f07">B. 网站内容</label></td>
										</tr>
										<tr class="even">
											<td><input type="radio"
												id="406635c6-a3cf-49b3-b08c-458cd5ee5765"
												name="88850302-4534-4dce-902d-73e0990b4e68[]"
												value="406635c6-a3cf-49b3-b08c-458cd5ee5765"><label
												for="406635c6-a3cf-49b3-b08c-458cd5ee5765">C. 建议与意见</label></td>
											<td><input type="radio"
												id="a989ce53-a664-41f2-ad61-01a7a4ce3222"
												name="88850302-4534-4dce-902d-73e0990b4e68[]"
												value="a989ce53-a664-41f2-ad61-01a7a4ce3222"><label
												for="a989ce53-a664-41f2-ad61-01a7a4ce3222">D. 不满</label></td>
										</tr>
										<tr class="odd">
											<td><input type="radio"
												id="b7a08697-0231-4e6f-81ba-cf4213170e5f"
												name="88850302-4534-4dce-902d-73e0990b4e68[]"
												value="b7a08697-0231-4e6f-81ba-cf4213170e5f"><label
												for="b7a08697-0231-4e6f-81ba-cf4213170e5f">E. 其他问题</label></td>
											<td></td>
										</tr>
									</tbody>
								</table>
							</li>
							<li class="part select" _require="1"
								_sn="d4261cbf-3e82-419d-bc24-d18c3e32382f">
								<h4 class="title">
									<span class="subject">（BUG)问题是关于</span> <span class="require">*</span>
									<label class="error"></label>
								</h4>
								<table class="options">
									<tbody>
										<tr class="odd">
											<td><input type="radio"
												id="cb66a2c8-07bb-4b8f-8be6-70440d9094a7"
												name="d4261cbf-3e82-419d-bc24-d18c3e32382f[]"
												value="cb66a2c8-07bb-4b8f-8be6-70440d9094a7"><label
												for="cb66a2c8-07bb-4b8f-8be6-70440d9094a7">A. 浏览器问题</label></td>
										</tr>
										<tr class="even">
											<td><input type="radio"
												id="c6405110-75f7-48a2-a7f8-7325d9981614"
												name="d4261cbf-3e82-419d-bc24-d18c3e32382f[]"
												value="c6405110-75f7-48a2-a7f8-7325d9981614"><label
												for="c6405110-75f7-48a2-a7f8-7325d9981614">B. 无法登陆</label></td>
										</tr>
										<tr class="odd">
											<td><input type="radio"
												id="3d34dc30-8cbb-440b-b3a7-c1df44b5fdbd"
												name="d4261cbf-3e82-419d-bc24-d18c3e32382f[]"
												value="3d34dc30-8cbb-440b-b3a7-c1df44b5fdbd"><label
												for="3d34dc30-8cbb-440b-b3a7-c1df44b5fdbd">C. 关于注册</label></td>
										</tr>
										<tr class="even">
											<td><input type="radio"
												id="8418222b-f041-496a-871c-253563cb1d7f"
												name="d4261cbf-3e82-419d-bc24-d18c3e32382f[]"
												value="8418222b-f041-496a-871c-253563cb1d7f"><label
												for="8418222b-f041-496a-871c-253563cb1d7f">D.
													网站设计及操作性上的问题</label></td>
										</tr>
										<tr class="odd">
											<td><input type="radio"
												id="6bcf9a5c-c4ca-490e-9a2a-7f077a8d24a5"
												name="d4261cbf-3e82-419d-bc24-d18c3e32382f[]"
												value="6bcf9a5c-c4ca-490e-9a2a-7f077a8d24a5"><label
												for="6bcf9a5c-c4ca-490e-9a2a-7f077a8d24a5">E. 其他</label></td>
										</tr>
									</tbody>
								</table>
							</li>
							<li class="part select" _require="1"
								_sn="8e5cdbc4-cc4a-4e20-8ca4-408233e7dce1">
								<h4 class="title">
									<span class="subject">（网站内容）问题是关于</span> <span class="require">*</span>
									<label class="error"></label>
								</h4>
								<table class="options">
									<tbody>
										<tr class="odd">
											<td><input type="radio"
												id="c93c8279-e989-4423-b82e-a45f42c3c594"
												name="8e5cdbc4-cc4a-4e20-8ca4-408233e7dce1[]"
												value="c93c8279-e989-4423-b82e-a45f42c3c594"><label
												for="c93c8279-e989-4423-b82e-a45f42c3c594">A. 缺少某些内容</label></td>
										</tr>
										<tr class="even">
											<td><input type="radio"
												id="392fe7b0-6076-471b-a505-860225340d18"
												name="8e5cdbc4-cc4a-4e20-8ca4-408233e7dce1[]"
												value="392fe7b0-6076-471b-a505-860225340d18"><label
												for="392fe7b0-6076-471b-a505-860225340d18">B. 错误的内容</label></td>
										</tr>
										<tr class="odd">
											<td><input type="radio"
												id="a1f4b220-cb7b-46ef-8fe5-258e4b7b0196"
												name="8e5cdbc4-cc4a-4e20-8ca4-408233e7dce1[]"
												value="a1f4b220-cb7b-46ef-8fe5-258e4b7b0196"><label
												for="a1f4b220-cb7b-46ef-8fe5-258e4b7b0196">C. 版权问题</label></td>
										</tr>
										<tr class="even">
											<td><input type="radio"
												id="ea957a87-7f43-47b1-9e54-4956d366815d"
												name="8e5cdbc4-cc4a-4e20-8ca4-408233e7dce1[]"
												value="ea957a87-7f43-47b1-9e54-4956d366815d"><label
												for="ea957a87-7f43-47b1-9e54-4956d366815d">D. 其他</label></td>
										</tr>
									</tbody>
								</table>
							</li>
							<li class="part select" _require="1"
								_sn="939ce2b4-b0c0-4dd1-8312-d8acf88a0671">
								<h4 class="title">
									<span class="subject">（建议与意见）是关于</span> <span class="require">*</span>
									<label class="error"></label>
								</h4>
								<table class="options">
									<tbody>
										<tr class="odd">
											<td><input type="radio"
												id="fae045e7-6b74-4a14-bcda-ba56c8e843bf"
												name="939ce2b4-b0c0-4dd1-8312-d8acf88a0671[]"
												value="fae045e7-6b74-4a14-bcda-ba56c8e843bf"><label
												for="fae045e7-6b74-4a14-bcda-ba56c8e843bf">A.
													无法获得必要的客服服务</label></td>
										</tr>
										<tr class="even">
											<td><input type="radio"
												id="e5c7fb0c-0326-412d-b477-e91f33bdd4f4"
												name="939ce2b4-b0c0-4dd1-8312-d8acf88a0671[]"
												value="e5c7fb0c-0326-412d-b477-e91f33bdd4f4"><label
												for="e5c7fb0c-0326-412d-b477-e91f33bdd4f4">B. 需求建议</label></td>
										</tr>
										<tr class="odd">
											<td><input type="radio"
												id="45f1e8f0-e734-4313-85b3-fbf0c521f828"
												name="939ce2b4-b0c0-4dd1-8312-d8acf88a0671[]"
												value="45f1e8f0-e734-4313-85b3-fbf0c521f828"><label
												for="45f1e8f0-e734-4313-85b3-fbf0c521f828">C. 网站内容相关</label></td>
										</tr>
										<tr class="even">
											<td><input type="radio"
												id="3482ff8d-6cdf-4591-b217-035569fc6261"
												name="939ce2b4-b0c0-4dd1-8312-d8acf88a0671[]"
												value="3482ff8d-6cdf-4591-b217-035569fc6261"><label
												for="3482ff8d-6cdf-4591-b217-035569fc6261">D.
													网站界面及设计相关</label></td>
										</tr>
										<tr class="odd">
											<td><input type="radio"
												id="38037390-f3c9-4195-a977-46ed5c9b2edd"
												name="939ce2b4-b0c0-4dd1-8312-d8acf88a0671[]"
												value="38037390-f3c9-4195-a977-46ed5c9b2edd"><label
												for="38037390-f3c9-4195-a977-46ed5c9b2edd">E. 其他</label></td>
										</tr>
									</tbody>
								</table>
							</li>
							<li class="part select" _require="1"
								_sn="12d4d388-4b2c-4400-a1ad-28e68dfb2f54">
								<h4 class="title">
									<span class="subject">（不满）是关于</span> <span class="require">*</span>
									<label class="error"></label>
								</h4>
								<table class="options">
									<tbody>
										<tr class="odd">
											<td><input type="radio"
												id="52e05c0e-3def-422b-905d-17d007cac790"
												name="12d4d388-4b2c-4400-a1ad-28e68dfb2f54[]"
												value="52e05c0e-3def-422b-905d-17d007cac790"><label
												for="52e05c0e-3def-422b-905d-17d007cac790">A.
													无法获得必要的客服服务</label></td>
										</tr>
										<tr class="even">
											<td><input type="radio"
												id="9ca12034-b5cd-4e56-b353-cd25883d5787"
												name="12d4d388-4b2c-4400-a1ad-28e68dfb2f54[]"
												value="9ca12034-b5cd-4e56-b353-cd25883d5787"><label
												for="9ca12034-b5cd-4e56-b353-cd25883d5787">B. 网站内容相关</label></td>
										</tr>
										<tr class="odd">
											<td><input type="radio"
												id="835e00c8-edc3-49c8-bc58-58c6ef8505a7"
												name="12d4d388-4b2c-4400-a1ad-28e68dfb2f54[]"
												value="835e00c8-edc3-49c8-bc58-58c6ef8505a7"><label
												for="835e00c8-edc3-49c8-bc58-58c6ef8505a7">C.
													网站界面及设计相关</label></td>
										</tr>
										<tr class="even">
											<td><input type="radio"
												id="f7d9b67d-6797-4c59-901b-41693c1ad878"
												name="12d4d388-4b2c-4400-a1ad-28e68dfb2f54[]"
												value="f7d9b67d-6797-4c59-901b-41693c1ad878"><label
												for="f7d9b67d-6797-4c59-901b-41693c1ad878">D. 其他</label></td>
										</tr>
									</tbody>
								</table>
							</li>
						</ol>
					</div>
				</div>
			</form>
	</div>

	<div id="questionnaire">
		<input type="hidden" name="responderId" value="<%=request.getParameter("id") %>"/>
		<input type="hidden" name="version" value="<%=request.getParameter("v") %>" />
		
		<div id="matrix"></div>
		
		<div id="matrixNet" class="separtor">
			<table id="matrixNetTable" border="1">
				<thead id="matrixNetTHead">
					<tr id="matrixNetTr">
						<th id="matrixNetTh">姓名</th>
					</tr>
				</thead>
				<tbody id="matrixNetTBody" ></tbody>
			</table>
		</div>
		
		<div id="matrixNetPlus" class="separtor">
			<table id="matrixNetPlusTable" border="1">
				<thead>
					<tr>
						<th id="matrixNetPlusTh"></th>
					</tr>
				</thead>
				<tbody id="matrixNetPlusTBody" ></tbody>
			</table>
		</div>
		
		<div id="normalQuestion"></div>
		
		<div id="selfInfo"></div>
		
		<!-- <input type="checkbox" title="aaa"> -->
	</div>
	<button id="submitBtn">提交</button>
</body>
</html>