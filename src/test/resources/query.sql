select a.version, count(a.question_id) questions from question a where not exists(select 1 from questionnaire_open b where a.version = b.version) group by a.version;

select * from QUESTION a where a.VERSION = 1331897286248 and a.TYPE = 1 group by a.TITLE;

select max(a.version) version from QUESTIONNAIRE_OPEN a;

select a.title, a.option_group_id from question a where a.version = 1331897286248 and a.type = 2 group by a.title, a.option_group_id;

select * from questionnaire;