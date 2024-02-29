import sqlite3
import datetime


def insert_telemetry_data(data, device_id):
    """
    Inserts telemetry data into the SQLite database for a given device.

    This function parses telemetry data for conductivity, pH, and water level measurements from a given data dictionary.
    It then inserts these measurements, along with a timestamp and device ID, into the telemetry table of the
    SQLite database.

    Parameters:
        data (dict): A dictionary containing lists of measurement dictionaries for 'conductivity', 'ph', and
                    'water level'.
                     Each measurement dictionary should contain a 'value' key with the measurement value and a 'ts' key
                     with the timestamp in milliseconds since epoch.
        device_id (str): The identifier of the device from which the telemetry data is collected.

    Returns:
        None
    """

    # Initialize variables
    conductivity = None
    ph = None
    water_level = None
    timestamp = None

    # Parse data and get the latest value for each parameter
    try:
        # Conductivity
        if 'conductivity' in data and len(data['conductivity']) > 0:
            latest_conductivity = data['conductivity'][0]
            conductivity = latest_conductivity['value']
            timestamp = datetime.datetime.fromtimestamp(latest_conductivity['ts'] / 1000.0)

        # pH
        if 'ph' in data and len(data['ph']) > 0:
            latest_ph = data['ph'][0]
            ph = latest_ph['value']
            # Use the same timestamp for all values
            timestamp = datetime.datetime.fromtimestamp(latest_ph['ts'] / 1000.0)

        # Water Level
        if 'water level' in data and len(data['water level']) > 0:
            latest_water_level = data['water level'][0]
            water_level = latest_water_level['value']
            # Use the same timestamp for all values
            timestamp = datetime.datetime.fromtimestamp(latest_water_level['ts'] / 1000.0)

        # Use context manager for database operations
        with sqlite3.connect('telemetry_data_smart_farming.db') as conn:
            cursor = conn.cursor()
            # Insert data into the database with device_id
            if timestamp:
                cursor.execute(
                    "INSERT INTO telemetry (timestamp, conductivity, ph, water_level, device_id) VALUES (?, ?, ?, ?, ?)",
                    (timestamp, conductivity, ph, water_level, device_id)
                )
                conn.commit()
    except sqlite3.Error as e:
        print(f"Database error: {e}")
    finally:
        conn.close()
