import json
from timetable_downloader import download_timetable
from datetime import datetime
import os
import requests

report = {
    'total_bus_stops': 0,
    'download_errors_count': 0,
    'download_errors_info': [],
}

BASE_DIR = os.path.dirname(os.path.abspath(__file__))

r = requests.get("https://triskelapps.com/apps/autobuses-jerez/bus_data.json")

if r.status_code == requests.codes.ok:

    data = r.text

    json_data = json.loads(data)

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
            ok = download_timetable(bus_line['id'], bus_stop['code'], BASE_DIR)

            if not ok:
                report['download_errors_count'] += 1
                report['download_errors_info'].append(f"{bus_line['id']}-{bus_stop['code']}")

with open(f'{BASE_DIR}/timetables_sync_report.log', 'a') as f2:
    timestamp = datetime.now().strftime('%Y-%m-%d %H:%M')
    f2.write(f'\n{timestamp}\n{report}\n')

print(f'DESCARGA FINALIZADA\n\n{report}')
