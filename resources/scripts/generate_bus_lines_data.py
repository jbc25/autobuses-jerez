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
	nonRegular = False


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


bus_lines_descriptions = {
	"1": "Esteve - San Telmo", 
	"2": "Esteve - Picadue\u00f1as", 
	"3": "Esteve - Las Torres", 
	"4": "Esteve - San Joaqu\u00edn - Hipercor", 
	"5": "Esteve - Guadalcac\u00edn", 
	"6": "Esteve - La Granja", 
	"7": "Angustias - Estella", 
	"8": "Circular 1 - Sentido antihorario", 
	"9": "Circular 2 - Sentido horario", 
	"10": "La Canaleja - Esteve - Hospital", 
	"11": "Esteve \u00bb La Marquesa", 
	"12": "Corredera - San José Obrero", 
	"13": "Alc\u00e1zar - Puertas del Sur - Asisa", 
	"14": "Esteve - Rotonda Nº6", 
	"15": "Nueva Jarilla - Guadalcac\u00edn - Angustias - El Portal", 
	"16": "Rotonda Casino - Hipercor - Ortega y Gasset", 
	"17": "Rotonda Casino - Montealto", 
	"18": "Corredera - Luz Shopping"
}

direction_names = {
	1: [
		{
			"destination": "Vallesequillo II",
			"direction": "San Telmo"
		},
		{
			"destination": "Pz. Angustias-Canal Sur",
			"direction": "Plaza Esteve"
		}
	],
	2: [
		{
			"destination": "Picadueña Baja",
			"direction": "Picadueñas"
		},
		{
			"destination": "Pz. Angustias-Canal Sur",
			"direction": "Plaza Esteve"
		}
	] ,
	3: [
		{
			"destination": "Av. Soleá",
			"direction": "Las Torres"
		},
		{
			"destination": "Pz. Angustias-Canal Sur",
			"direction": "Plaza Esteve"
		}
	] ,
	4: [
		{
			"destination": "Guatemala II",
			"direction": "Hipercor"
		},
		{
			"destination": "Pz. Angustias-Canal Sur",
			"direction": "Plaza Esteve"
		}
	],
	5: [
		{
			"destination": "C/ Divina Pastora",
			"direction": "Guadalcacín"
		},
		{
			"destination": "Diego F. Herrera",
			"direction": "La Corredera"
		}
	],
	6: [
		{
			"destination": "Av. de Europa",
			"direction": "La Granja"
		},
		{
			"destination": "Pz. Angustias-Canal Sur",
			"direction": "Plaza Esteve"
		}
	],
	7: [
		{
			"destination": "C/ del Pozo",
			"direction": "Estella"
		},
		{
			"destination": "C/ Cartuja",
			"direction": "Pz. Angustias-Bodega"
		}
	],
	10: [
		{
			"destination": "Consultas Externas",
			"direction": "Hospital"
		},
		{
			"destination": "Parque Eroski",
			"direction": "La Canaleja"
		}
	],
	11: [
		{
			"destination": "C/ Alcázar de Jerez I",
			"direction": "La Marquesa"
		},
		{
			"destination": "Pz. Angustias-Canal Sur",
			"direction": "Plaza Esteve"
		}
	],
	12: [
		{
			"destination": "Arroyo Membrillar",
			"direction": "San José Obrero"
		},
		{
			"destination": "Diego F. Herrera",
			"direction": "Corredera"
		}
	],
	13: [
		{
			"destination": "Vallesequillo II",
			"direction": "Zona Sur"
		},
		{
			"destination": "Torresoto",
			"direction": "Alcázar-Centro"
		}
	],
	14: [
		{
			"destination": "El Encinar",
			"direction": "Rotonda nº 6"
		},
		{
			"destination": "Pz. Angustias-Canal Sur",
			"direction": "Plaza Esteve"
		}
	],
	15: [
		{
			"destination": "Estaciones",
			"direction": "Pz. Angustias-El Portal"
		},
		{
			"destination": "Recauchutados Córdoba",
			"direction": "El Portal"
		},
		{
			"destination": "Madre de Dios",
			"direction": "Pz. Angustias-N.Jarilla"
		},
		{
			"destination": "Nueva Jarilla II",
			"direction": "Nueva Jarilla"
		}
	],
	16: [
		{
			"destination": "Las Flores",
			"direction": "Ortega y Gasset"
		},
		{
			"destination": "Santo Domingo",
			"direction": "Rotonda Casinos"
		}
	],
	17: [
		{
			"destination": "C/ Doctor Arruga",
			"direction": "Montealto"
		},
		{
			"destination": "Santo Domingo",
			"direction": "Rotonda Casinos"
		}
	],
	18: [
		{
			"destination": "Alcampo",
			"direction": "Luz Shopping"
		},
		{
			"destination": "Diego F. Herrera",	
			"direction": "Corredera"
		}
	]
}


non_regular_bus_stops = ['Garvey', 'Los Villares', 'Ciudad del Transporte', 'IVECO', 'PCTA', 'Campo de la Juventud', 'Agrimensor',
	'Alvar Fañez', 'Rodrigo de Jerez', 'Media Markt', 'Guadabajaque', 'Campus El Sabio', 'Resid. Rancho Colores', 
	'Gta. de Guadabajaque', 'Media Markt', 'Rodrigo de Jerez', 'Carrefour Sur.'
]

timetable_download_count = 0

def download_timetable(line_number, bus_stop_code, bus_stop_name):

	global timetable_download_count
	timetable_download_count += 1

	print(f'Recibiendo horarios linea{line_number}-parada{bus_stop_code}-{bus_stop_name.replace("/","")}')

	payload = {'valorLinea': line_number, 'valorCaja1': bus_stop_code}
	r = requests.post("https://www.jerez.es/index.php?id=listar_b", data=payload)
	html_str = r.text

	file_out = open(f'timetables/linea{line_number}-parada{bus_stop_code}-{bus_stop_name.replace("/","")}', 'w')
	file_out.write(html_str)
	file_out.close()

	print(f'Horarios guardados. total: {timetable_download_count}')


lines_files = ["1-5", "6-10", "11-14", "15-18"]

# These lines has 2 LineString
repeat_path_line_15 = True

for lines_file in lines_files:

	print(f'Procesando archivo lineas {lines_file}')

	starting_line_number = int(lines_file.split('-')[0])
	line_number = starting_line_number

	print(f'Línea de comienzo {starting_line_number}')

	file_geojson = open(f'geojson/lineas_{lines_file}.geojson', 'r+')
	geojson_text = file_geojson.read()
	geojson_text = geojson_text.replace('COD. PARADA', 'COD').replace('COD.PARADA', 'COD').replace('Codigo de parada', 'COD')
	file_geojson.close()

	geojson = json.loads(geojson_text)

	features = geojson['features']


	bus_line = BusLine()
	bus_line.path = []

	bus_stops = []

	index = 0;
	direction_index = 0

	for feature in features:

		index = index + 1

		geometry = feature['geometry']
		type_geometry = geometry['type']

		if type_geometry == 'Point':

			properties_bus_stop = feature['properties']
			bus_stop_name = properties_bus_stop['name'].strip()

			bus_stop_name_and_line_number = f'L{line_number}-{bus_stop_name}'
#			if bus_stop_name_and_line_number in fake_bus_stops:
#				continue


			bus_stop = BusStop()
			bus_stop.name = bus_stop_name
			bus_stop.lineId = line_number
			bus_stop.nonRegular = bus_stop_name in non_regular_bus_stops

			# if 'Angustias' in bus_stop_name:
			#	print(f'Angustias encontrado. nombre: {bus_stop_name} Linea: {line_number}')

			try:
				if 'COD' in properties_bus_stop:
					bus_stop.code = int(float(properties_bus_stop['COD']))
					#print(f'bus_stop.code: {bus_stop.code}')
				else:
					bus_stop.code = -1
					print('no COD')

			except:
				bus_stop.code = -1
				print(f'Code not found. Línea: {line_number}. parada: {bus_stop_name}. index: {index}')
				

#			download_timetable(line_number, bus_stop.code, bus_stop_name)

			if 'TRANSBORDO' in properties_bus_stop:
				bus_stop.transfer = properties_bus_stop['TRANSBORDO'].strip()

			#if 'SENTIDO' in properties_bus_stop:

			if line_number == 8:
				bus_stop.direction = 'Sentido antihorario'
			elif line_number == 9:
				bus_stop.direction = 'Sentido horario'
			else:
				# print(f'line_number: {line_number}, direction_index: {direction_index}')
				current_direction_data = direction_names[line_number][direction_index]
				bus_stop.direction = current_direction_data['direction']
				
				bus_stop_name_to_continue = current_direction_data['destination']
				if bus_stop.name == bus_stop_name_to_continue and direction_index == 0:
					direction_index += 1;

			coordinates = geometry['coordinates']
			bus_stop.coordinates = [float(coordinates[1]), float(coordinates[0])]

			bus_stops.append(bus_stop.__dict__)

		elif type_geometry == 'LineString':

			properties_line = feature['properties']
			bus_line.name = properties_line['name'].replace(' Regular', '')
			bus_line.id = line_number
			bus_line.finalBusStopCode = final_bus_stop_code_switcher.get(line_number)

			
			bus_line.description = bus_lines_descriptions[str(line_number)]

			coordinates_list = geometry['coordinates']


			if bus_line.path:
				print(f'Mas de un LineString en linea {line_number}. Se añadiran al anterior')

			print(f'coordinates_list size: {len(coordinates_list)}')

			for coordinates in coordinates_list:
				bus_line.path.append([float(coordinates[1]), float(coordinates[0])])


			# After a LineString a new bus_line starts with these exceptions

			if line_number == 15 and repeat_path_line_15:
				repeat_path_line_15 = False
				continue


			line_number = line_number + 1
			print(f'Línea siguiente {line_number}')


			bus_line.color = 0
			bus_line.busStops = bus_stops
			bus_lines.append(bus_line)

			bus_line = BusLine()
			bus_line.path = []
			bus_stops = []

			direction_index = 0


serialized_data = json.dumps([ob.__dict__ for ob in bus_lines])
#print(serialized_data)


import pyperclip
pyperclip.copy(serialized_data)