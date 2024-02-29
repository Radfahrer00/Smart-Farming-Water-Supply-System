import sqlite3

'''
This script is designed to interact with an SQLite database for storing telemetry data related to smart farming.
It establishes a connection to an SQLite database, creates a new table for storing telemetry data if it does not
exist, and then closes the connection. The purpose is to prepare the database for subsequent data insertion operations
related to smart farming activities such as monitoring conductivity, pH levels, and water levels.
'''

# Specify the name of the SQLite database file
db_file = 'telemetry_data_smart_farming.db'

# SQL command to create a new table
create_table_sql = """
CREATE TABLE IF NOT EXISTS telemetry (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    timestamp DATETIME,
    conductivity REAL,
    ph REAL,
    water_level REAL,
    device_id TEXT
);
"""

# Connect to the SQLite database
conn = sqlite3.connect(db_file)

# Create a cursor object using the connection
cursor = conn.cursor()

# Execute the SQL command to create the table
cursor.execute(create_table_sql)

# Commit the changes to the database
conn.commit()

# Close the connection to the database
conn.close()

print("Table created successfully.")
