var ctx = '';
$(function() {
	ctx = $('input[name=ctx]').val();

	var matrix = {};
	var matrixNet = {};
	var normal = {};
	
	var url4paper = ctx + '/controller?action=getQuestionnaireOpenPaper';
	$.getJSON(url4paper, function(result) {
		//console.debug(url4paper);
		//console.debug(result);
		
		matrix = result.matrix;
		matrixNet = result.matrixNet;
		normal = result.normal;
		
		//matrix
		for(var index = 0; index < matrix.length; index++) {
			var question = matrix[index];
			var title = question.title;
			
			$('.content').append('<li class="part select" ><h4 class=title ><span class=subject >'+title+'</span></h4><table></table></li>');
		}
		
		//matrix net
		$('.content').append('<li class="part select" ><h4 class=title ><span class=subject >请选择您所填写的这些人的相关信息 Matrix Net</span></h4><li class="ui-state-default ui-corner-all" title=".ui-icon-plus"><span class="ui-icon ui-icon-plus"></span></li><table></table></li>');
		
		//normal
		for(var groupIndex = 0; groupIndex < normal.length; groupIndex++) {
			var group = normal[groupIndex];
			$('.content').append('<div><h3>'+group.title+'</h3></div>');
			for(var index = 0; index < group.questions.length; index++) {
				var question = group.questions[index];
				$('.content').append('<li class="part select" ><h4 class=title ><span class=subject >'+question.content+'</span></h4><table></table></li>');
			}
		}
	});
	
});