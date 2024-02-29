import requests
import json


def get_user_token(email, password):
    """
    Retrieves a user authentication token from a specified server using the provided email and password.

    Parameters:
        email (str): The user's email address used for authentication.
        password (str): The user's password for authentication.

    Returns:
        str: A user authentication token if the request is successful, None otherwise.
    """

    url = 'https://srv-iot.diatel.upm.es/api/auth/login'
    headers = {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
    }
    data = {
        'username': email,
        'password': password
    }

    response = requests.post(url, headers=headers, json=data)

    if response.status_code == 200:
        return response.json().get('token')
    else:
        print(f"Error fetching token: {response.status_code}")
        print(response.text)
        return None


def fetch_telemetry_data(token, device_id):
    """
    Fetches telemetry data for a specific device from a server using an authentication token.

    Parameters:
        token (str): The user's authentication token required to access the telemetry data.
        device_id (str): The unique identifier of the device for which telemetry data is being requested.

    Returns:
        dict: A dictionary containing the telemetry data if the request is successful, None otherwise.
    """

    url_base = 'https://srv-iot.diatel.upm.es/api/plugins/telemetry/DEVICE'
    url = f'{url_base}/{device_id}/values/timeseries'
    headers = {
        'Content-Type': 'application/json',
        'X-Authorization': f'Bearer {token}'
    }

    try:
        response = requests.get(url, headers=headers)
        response.raise_for_status()
        data = response.json()
        print(json.dumps(data, indent=4))
        return data
    except requests.RequestException as e:
        print(f"Error fetching data from Thingsboard: {e}")
        return None
