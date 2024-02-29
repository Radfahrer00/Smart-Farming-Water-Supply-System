import time
import getpass
from thingsboard_client import fetch_telemetry_data, get_user_token
from database_manager import insert_telemetry_data


def main():
    """
    Main function to fetch telemetry data from Thingsboard for specified devices and insert it into a local database.

    Prompts the user for email and password to authenticate with the Thingsboard server and retrieve an access token.
    Uses this token to fetch telemetry data for each device specified in `device_ids` list at 5-second intervals.
    The fetched data is then inserted into a local SQLite database using the `insert_telemetry_data` function.
    If the token is not retrieved successfully, the program will notify the user and terminate.

    Args:
        None

    Returns:
        None
    """
    email = input("Enter your email: ")
    password = getpass.getpass("Enter your password: ")
    token = get_user_token(email, password)
    if token:
        device_ids = ['925dbb80-ba15-11ee-8027-c77be3144608', 'ac9ab050-ba21-11ee-8027-c77be3144608']
        while True:
            for device_id in device_ids:
                data = fetch_telemetry_data(token, device_id)
                if data is not None:
                    insert_telemetry_data(data, device_id)
                else:
                    print(f"Failed to fetch data for device {device_id} from Thingsboard.")
            time.sleep(5)  # Sleep for 5 seconds
    else:
        # This block will execute if the token is not retrieved (token is falsy, likely None).
        print("Failed to retrieve token. Please check your credentials and try again.")


if __name__ == "__main__":
    main()
