import json
import requests



report = {
	'total_bus_stops': 0,
	'download_errors_count': 0,
	'download_errors_info': [],
}

def download_timetable(line_number, bus_stop_code):

	print(f'linea{line_number}-parada{bus_stop_code}')

	payload = {'valorLinea': line_number, 'valorCaja1': bus_stop_code}
	r = requests.post("https://www.comujesa.es/index.php?id=listar_b", data=payload)

	if r.status_code == requests.codes.ok:

		html_str = r.text

		with open(f'timetables/L{line_number}-P{bus_stop_code}.html', 'w') as file_out:
			file_out.write(html_str)

	else:
		report['download_errors_count'] += 1
		report['download_errors_info'].append('L{line_number}-P{bus_stop_code}')
		print(f'error downloading L{line_number}-P{bus_stop_code}')


with open('bus_lines_data.json', 'r') as f:
	
	json_data = json.loads(f.read())

	total_bus_stops = 0
	for bus_line in json_data:
		for bus_stop in bus_line['busStops']:
			total_bus_stops += 1

	report['total_bus_stops'] = total_bus_stops

	# Download timetables loop
	current_bus_stop = 0
	for bus_line in json_data:
		for bus_stop in bus_line['busStops']:
			current_bus_stop += 1
			print(f'Descargando horario {current_bus_stop} de {total_bus_stops}')
			download_timetable(bus_line['id'], bus_stop['code'])

	print(f'DESCARGA FINALIZADA\n\n{report}')
