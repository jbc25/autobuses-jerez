import chevron
import json

content_file = open('content.json', 'r+')
content_json = json.loads(content_file.read())

templates = [
	'index', # 'about', 'services', 'contact',	
]

for template in templates:
	template_file_name = f'{template}-template.html'
	template_text = open(template_file_name, 'r+').read()
	rendered = chevron.render(template_text, content_json)

	file_out = open(f'public/{template}.html', 'w')
	file_out.write(rendered)
	file_out.close()
