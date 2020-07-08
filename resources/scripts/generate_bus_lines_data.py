import json
import requests

class BusLine:
	id = 0
	name = ''
	description = ''
	color = 0
	busStops = []
	path = []
	finalBusStopCode = 0



class BusStop:
	name = ''
	code = 0
	lineId = 0
	transfer = ''
	direction = ''
	way = ''
	coordinates = []


final_bus_stop_code_switcher = {
	1:401, # San Telmo
	2:334, # Picadueña baja
	3:261, # Las Torres
	4:206, # Hipercor
	5:195, # Guadalcacín
	6:60, # Avda. Europa
	7:172, # "Estella"
	8:-1, # Circulares
	9:-1, # Circulares
	10:447, # Urgencias
	11:243, # La Marquesa
	12:61, # Avda. Europa
	13:41, # Asisa
	14:384, # Rotonda 6
	15:157, # El Portal
	16:313, # Ortega y Gasset
	17:301, # Montealto
	18:32, # Area Sur
}


bus_lines = []
all_bus_stops = {} # Algunas paradas no tienen código. Este diccionario se crea dinánicamente para asignar el código de otras lineas
pending_bus_stop_codes = {
	'L12-Estadio Chapín': 484,
	'L12-Los Pinos': 486,
	'L12-San Marino': 488,
	'L12-Carrefour Norte': 105,
	'L12-Arrumbadores': 39,
	'L14-Canal Sur - Angustias': 130,
	'L15-AV. de la Paz':55,

}

fake_bus_stops = [
	'L14-Rotonda nº 4',
	'L15-Ortega y Gasset',
	'L15-San Juan Bautista',
	'L15-Diego Fernández Herrera',
	'L15-Hipercor - Camino de Espera',
	'L15-Pozoalbero',


]

def download_timetable(line_number, bus_stop_code, bus_stop_name):

	print(f'Recibiendo horarios linea{line_number}-parada{bus_stop_code}-{bus_stop_name.replace("/","")}')

	payload = {'valorLinea': line_number, 'valorCaja1': bus_stop_code}
	r = requests.post("https://www.jerez.es/index.php?id=listar_b", data=payload)
	html_str = r.text

	file_out = open(f'timetables/linea{line_number}-parada{bus_stop_code}-{bus_stop_name.replace("/","")}', 'w')
	file_out.write(html_str)
	file_out.close()

	print('Horarios guardados')



for line_number in range(1, 19):

	print(f'Procesando linea {line_number}')

	bus_line = BusLine()
	bus_line.path = []

	file_geojson = open(f'geojson/linea{line_number}.geojson', 'r+')
	geojson_text = file_geojson.read()
	geojson_text = geojson_text.replace('COD. PARADA', 'COD').replace('COD.PARADA', 'COD')
	file_geojson.close()

	geojson = json.loads(geojson_text)

	features = geojson['features']

	bus_stops = []

	index = 0;

	for feature in features:

		index = index + 1

		geometry = feature['geometry']
		type_geometry = geometry['type']

		if type_geometry == 'Point':

			properties_bus_stop = feature['properties']
			bus_stop_name = properties_bus_stop['name'].strip()

			bus_stop_name_and_line_number = f'L{line_number}-{bus_stop_name}'
			if bus_stop_name_and_line_number in fake_bus_stops:
				continue


			bus_stop = BusStop()
			bus_stop.name = bus_stop_name
			bus_stop.lineId = line_number

			# if 'Angustias' in bus_stop_name:
			#	print(f'Angustias encontrado. nombre: {bus_stop_name} Linea: {line_number}')

			try:
				if 'COD' in properties_bus_stop:
					bus_stop.code = int(float(properties_bus_stop['COD']))
					all_bus_stops[bus_stop_name] = bus_stop.code
			except:
				if bus_stop_name in all_bus_stops:
					bus_stop.code = all_bus_stops[bus_stop_name]

				elif bus_stop_name_and_line_number in pending_bus_stop_codes:
					bus_stop.code = pending_bus_stop_codes[bus_stop_name_and_line_number]
					# print(f'Code in DICTIONARY. Línea: {line_number}. parada: {bus_stop_name}')

				else:
					bus_stop.code = -1
					print(f'Code not found. Línea: {line_number}. parada: {bus_stop_name}. index: {index}')
				

			if line_number >= 5:
				download_timetable(line_number, bus_stop.code, bus_stop_name)

			bus_stop.transfer = properties_bus_stop['TRANSBORDO'].strip()
			bus_stop.direction = properties_bus_stop['SENTIDO'].strip()

			coordinates = geometry['coordinates']
			bus_stop.coordinates = [float(coordinates[1]), float(coordinates[0])]

			bus_stops.append(bus_stop.__dict__)

		elif type_geometry == 'LineString':

			properties_line = feature['properties']
			bus_line.name = properties_line['name']
			bus_line.id = line_number
			bus_line.finalBusStopCode = final_bus_stop_code_switcher.get(line_number)

			if 'description' in properties_line:
				bus_line.description = properties_line['description'].strip()
			#else:
			#	print(f'Linea {line_number}, un LineString no tiene descripción')

			coordinates_list = geometry['coordinates']


			if bus_line.path:
				print(f'Mas de un LineString en linea {line_number}. Se añadiran al anterior')

			print(f'coordinates_list size: {len(coordinates_list)}')

			for coordinates in coordinates_list:
				bus_line.path.append([float(coordinates[1]), float(coordinates[0])])


			

	bus_line.color = 0
	bus_line.busStops = bus_stops
	bus_lines.append(bus_line)


serialized_data = json.dumps([ob.__dict__ for ob in bus_lines])
#print(serialized_data)


import pyperclip
pyperclip.copy(serialized_data)