import os
import sys
import time

import bcrypt
import mysql.connector
from faker import Faker
from datetime import datetime, timedelta
import random
from decimal import Decimal, ROUND_DOWN
import uuid

# Database connection configuration
db_config = {
    "host": os.environ.get("DB_HOST", "localhost"),
    "port": int(os.environ.get("DB_PORT", "3306")),
    "user": os.environ.get("DB_USER", "likeme_mod"),
    "password": os.environ["DB_PASSWORD"],
    "database": os.environ.get("DB_NAME", "likeme"),
    "auth_plugin": "caching_sha2_password",
    "use_pure": True,
}

# Every seeded account shares this password; the three demo accounts below are the ones to log in with.
DEMO_PASSWORD = os.environ.get("DEMO_PASSWORD", "demo1234")
DEMO_ADMIN = "demo_admin"
DEMO_CLIENT = "demo_client"
DEMO_INFLUENCER = "demo_influencer"

random.seed(42)
fake = Faker(["en_US", "en_GB", "en_CA", "en_AU"])
Faker.seed(42)

# Constants
SALT = bcrypt.gensalt(12)
# Spring's BCrypt reads $2b$ too, but $2a$ matches what the API itself writes.
PASSWORD_HASH = bcrypt.hashpw(DEMO_PASSWORD.encode(), SALT).decode().replace("$2b$", "$2a$", 1)
SALT = SALT.decode().replace("$2b$", "$2a$", 1)
OFFER_IMAGES = {
    "LIKE": "https://images.unsplash.com/photo-1665470909901-162912ec16f7?q=80&w=1412&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    "COMMENT": "https://images.unsplash.com/photo-1611162617213-7d7a39e9b1d7",
    "FOLLOW": "https://images.unsplash.com/photo-1634942537040-f7ba41298016?q=80&w=1480&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
}

# Add these constants
UNSPLASH_URLS = [
    "https://images.unsplash.com/photo-1611262588024-d12430b98920",
    "https://images.unsplash.com/photo-1611162617213-7d7a39e9b1d7",
    "https://images.unsplash.com/photo-1611083360739-bdad6e0eb1fa",
    "https://images.unsplash.com/photo-1472289065668-ce650ac443d2",
    # Add more URLs as needed
]


def get_random_users(count):
    """Offline stand-in for the randomuser.me API, returning the same shape."""
    users = []
    for _ in range(count):
        gender = random.choice(["men", "women"])
        local = fake[random.choice(fake.locales)]
        first, last = local.first_name(), local.last_name()
        users.append({
            "login": {"username": f"{first}{last}".lower()[:16]},
            "email": f"{first}.{last}@example.com".lower(),
            "picture": {"large": f"https://randomuser.me/api/portraits/{gender}/{random.randint(0, 99)}.jpg"},
            "name": {"first": first, "last": last},
            "location": {
                "country": local.current_country(),
                "street": {"number": local.building_number(), "name": local.street_name()},
                "city": local.city(),
                "state": local.administrative_unit(),
                "postcode": local.postcode(),
            },
        })
    return users


def create_unique_username(base_username):
    return f"{base_username}{str(uuid.uuid4())[:2]}"


def create_unique_email(email):
    name, domain = email.split("@")
    return f"{name}_{str(uuid.uuid4())[:2]}@{domain}"


def create_unique_instagram(username):
    return f"{username}{str(uuid.uuid4())[:2]}"  # Removed @ symbol


def create_unique_phone():
    # Generate a random phone number in format: +1-XXX-XXX-XXXX
    area = random.randint(100, 999)
    prefix = random.randint(100, 999)
    line = random.randint(1000, 9999)
    return f"+1-{area}-{prefix}-{line}"


def create_connection():
    return mysql.connector.connect(**db_config)


def insert_admins(cursor):
    users = get_random_users(2)
    admin_sql = """
    INSERT INTO admins (
        username, 
        email, 
        password, 
        salt, 
        permissions, 
        profile_photo_path, 
        is_active,
        created_on,
        updated_on
    )
    VALUES (%s, %s, %s, %s, %s, %s, %s, NOW(), NOW())
    """
    # First admin with FULL permissions
    cursor.execute(
        admin_sql,
        (
            create_unique_username(users[0]["login"]["username"]),
            create_unique_email(users[0]["email"]),
            PASSWORD_HASH,
            SALT,
            "FULL",
            users[0]["picture"]["large"],
            True,
        ),
    )
    # Second admin with MODERATOR permissions
    cursor.execute(
        admin_sql,
        (
            create_unique_username(users[1]["login"]["username"]),
            create_unique_email(users[1]["email"]),
            PASSWORD_HASH,
            SALT,
            "MODERATOR",
            users[1]["picture"]["large"],
            True,
        ),
    )
    return cursor.lastrowid


def insert_applications_and_influencers(cursor, num_pending=10, num_approved=35):
    # First, handle approved applications and their influencers
    approved_users = get_random_users(num_approved)
    approved_app_ids = []

    # Insert approved applications first
    for user in approved_users:
        username = create_unique_username(user["login"]["username"])
        app_sql = """
        INSERT INTO influencer_applications (
            username, email, phone_number, about, instagram_handle,
            profile_photo_path, cover_photo_path, is_approved, 
            billing_first_name, billing_last_name, billing_country,
            billing_street_address, billing_city, billing_state, 
            billing_zip_code, created_on, updated_on
        )
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, NOW(), NOW())
        """
        cursor.execute(
            app_sql,
            (
                username,
                create_unique_email(user["email"]),
                create_unique_phone(),
                f"Professional influencer specializing in social media growth and engagement",
                create_unique_instagram(username),
                user["picture"]["large"],
                random.choice(UNSPLASH_URLS),  # cover_photo_path
                True,  # is_approved = True for these applications
                user["name"]["first"],
                user["name"]["last"],
                user["location"]["country"],
                f"{user['location']['street']['number']} {user['location']['street']['name']}",
                user["location"]["city"],
                user["location"]["state"],
                str(user["location"]["postcode"]),
            ),
        )
        app_id = cursor.lastrowid
        approved_app_ids.append(app_id)

        # Create influencer account for each approved application
        inf_sql = """
        INSERT INTO influencers (
            application_id, password, salt, is_instagram_connected, 
            instagram_access_token, status, is_active, created_on, updated_on
        )
        VALUES (%s, %s, %s, %s, %s, %s, %s, NOW(), NOW())
        """
        cursor.execute(
            inf_sql,
            (
                app_id,
                PASSWORD_HASH,
                SALT,
                True,
                "27252534-01e3-487d-9ef9-7af437e8e1a0",  # Fixed instagram_access_token
                "ACTIVE",
                True,
            ),
        )

    # Then handle pending applications
    pending_users = get_random_users(num_pending)

    # Insert pending applications
    for user in pending_users:
        username = create_unique_username(user["login"]["username"])
        app_sql = """
        INSERT INTO influencer_applications (
            username, email, phone_number, about, instagram_handle,
            profile_photo_path, cover_photo_path, is_approved, 
            billing_first_name, billing_last_name, billing_country,
            billing_street_address, billing_city, billing_state, 
            billing_zip_code, created_on, updated_on
        )
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, NOW(), NOW())
        """
        cursor.execute(
            app_sql,
            (
                username,
                create_unique_email(user["email"]),
                create_unique_phone(),
                f"Professional influencer specializing in social media growth and engagement",
                create_unique_instagram(username),
                user["picture"]["large"],
                random.choice(UNSPLASH_URLS),  # cover_photo_path
                False,  # is_approved = False for pending applications
                user["name"]["first"],
                user["name"]["last"],
                user["location"]["country"],
                f"{user['location']['street']['number']} {user['location']['street']['name']}",
                user["location"]["city"],
                user["location"]["state"],
                str(user["location"]["postcode"]),
            ),
        )

    return approved_app_ids


def insert_clients(cursor, num_clients=100):
    users = get_random_users(num_clients)
    client_ids = []

    for user in users:
        username = create_unique_username(user["login"]["username"])
        client_sql = """
        INSERT INTO clients (username, email, password, salt, profile_photo_path, instagram_handle,
        is_instagram_connected, instagram_access_token, billing_first_name, billing_last_name, billing_country,
        billing_street_address, billing_city, billing_state, billing_zip_code, is_active,
        created_on, updated_on)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, NOW(), NOW())
        """
        cursor.execute(
            client_sql,
            (
                username,
                create_unique_email(user["email"]),
                PASSWORD_HASH,
                SALT,
                user["picture"]["large"],
                create_unique_instagram(username),
                True,
                generate_instagram_token(),
                user["name"]["first"],
                user["name"]["last"],
                user["location"]["country"],
                f"{user['location']['street']['number']} {user['location']['street']['name']}",
                user["location"]["city"],
                user["location"]["state"],
                str(user["location"]["postcode"]),
                True,
            ),
        )
        client_ids.append(cursor.lastrowid)
    return client_ids


def insert_offers(cursor, influencer_ids):
    offer_ids = []
    offer_descriptions = {
        "LIKE": "Get authentic likes from real users to boost your engagement",
        "COMMENT": "Receive meaningful comments to increase post interaction",
        "FOLLOW": "Grow your follower base with real, engaged followers",
    }

    # First verify that all influencer IDs exist
    cursor.execute(
        """
        SELECT id FROM influencers 
        WHERE id IN ({})
        """.format(
            ",".join(["%s"] * len(influencer_ids))
        ),
        influencer_ids,
    )
    valid_influencer_ids = [x[0] for x in cursor.fetchall()]

    if not valid_influencer_ids:
        raise Exception("No valid influencer IDs found")

    for inf_id in valid_influencer_ids:
        for offer_type in ["LIKE", "COMMENT", "FOLLOW"]:
            price = Decimal(random.randint(30, 500)).quantize(
                Decimal("1."), rounding=ROUND_DOWN
            )
            random_date = get_random_date_2024()
            offer_sql = """
            INSERT INTO offers (
                title, 
                description, 
                cover_photo_path, 
                type, 
                price, 
                is_active,
                created_by_id, 
                updated_by_id,
                created_on, 
                updated_on
            )
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
            """
            cursor.execute(
                offer_sql,
                (
                    f"{offer_type.capitalize()} {random.choice(['Service', 'Offer', 'Package'])}",
                    offer_descriptions[offer_type],
                    OFFER_IMAGES[offer_type],
                    offer_type,
                    price,
                    True,
                    inf_id,
                    inf_id,  # Set updated_by_id to same as created_by_id
                    random_date,
                    random_date,
                ),
            )
            offer_ids.append(cursor.lastrowid)

    # Verify offers were created
    if not offer_ids:
        raise Exception("Failed to create any offers")

    return offer_ids


def insert_orders_and_invoices(cursor, offer_ids, client_ids, num_orders=100):
    # Get offer details including price
    cursor.execute(
        """
        SELECT o.id, o.type, o.created_by_id, o.price 
        FROM offers o
        WHERE o.id IN ({})
    """.format(
            ",".join(["%s"] * len(offer_ids))
        ),
        offer_ids,
    )

    offer_info = {
        row[0]: {"type": row[1], "influencer_id": row[2], "price": row[3]}
        for row in cursor.fetchall()
    }

    for _ in range(num_orders):
        offer_id = random.choice(offer_ids)
        offer = offer_info[offer_id]
        random_date = get_random_date_2024()

        # Create invoice with exact offer price
        invoice_sql = """
        INSERT INTO invoices (amount, status, created_on, updated_on)
        VALUES (%s, %s, %s, %s)
        """
        invoice_status = random.choice(["PENDING", "PAID"])
        cursor.execute(
            invoice_sql,
            (
                offer["price"],
                invoice_status,
                random_date,
                random_date,
            ),
        )
        invoice_id = cursor.lastrowid

        # Prepare post_id and comment based on offer type
        post_id = None
        comment = None
        if offer["type"] in ["LIKE", "COMMENT"]:
            post_id = f"post_{str(uuid.uuid4())[:8]}"
        if offer["type"] == "COMMENT":
            comment = f"Great post! {str(uuid.uuid4())[:8]}"

        # Set order status based on invoice status
        order_status = "PENDING"
        if invoice_status == "PAID":
            order_status = random.choice(["PENDING", "COMPLETE"])

        # Create order
        order_sql = """
        INSERT INTO orders (
            offer_id, 
            post_id, 
            comment, 
            invoice_id, 
            status, 
            ordered_by_id, 
            updated_by_id,
            created_on, 
            updated_on
        )
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
        """
        cursor.execute(
            order_sql,
            (
                offer_id,
                post_id,
                comment,
                invoice_id,
                order_status,
                random.choice(client_ids),
                offer["influencer_id"],
                random_date,
                random_date,
            ),
        )


def insert_platform_settings(cursor, admin_id):
    random_date = get_random_date_2024()
    settings_sql = """
    INSERT INTO platform_settings (type, value, updated_on, updated_by)
    VALUES (%s, %s, %s, %s)
    """
    cursor.execute(settings_sql, ("COMMISSION_PERCENTAGE", "10", random_date, admin_id))


def get_random_date_2024():
    start_date = datetime(2024, 1, 1)
    end_date = datetime(2024, 12, 31)
    time_between_dates = end_date - start_date
    days_between_dates = time_between_dates.days
    random_number_of_days = random.randrange(days_between_dates)
    random_date = start_date + timedelta(days=random_number_of_days)
    return random_date


def generate_instagram_token():
    return f"IGQVJYeXpoZA{str(uuid.uuid4()).replace('-', '')[:40]}"


def wait_for_schema(attempts=60):
    """The API applies the Flyway migrations on boot; wait until they have landed."""
    for _ in range(attempts):
        try:
            conn = create_connection()
            cursor = conn.cursor()
            cursor.execute("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = %s AND table_name = 'orders'", (db_config["database"],))
            if cursor.fetchone()[0]:
                return conn
            conn.close()
        except mysql.connector.Error:
            pass
        time.sleep(2)
    sys.exit("Schema never appeared; is the API running?")


def promote_demo_accounts(cursor):
    """Give one account per role a fixed, documented username."""
    cursor.execute("UPDATE admins SET username = %s, email = 'admin@likeme.demo' ORDER BY id LIMIT 1", (DEMO_ADMIN,))
    cursor.execute("UPDATE clients SET username = %s, email = 'client@likeme.demo' ORDER BY id LIMIT 1", (DEMO_CLIENT,))
    cursor.execute(
        "UPDATE influencer_applications SET username = %s, email = 'influencer@likeme.demo' "
        "WHERE is_approved = TRUE ORDER BY id LIMIT 1",
        (DEMO_INFLUENCER,),
    )


def main():
    conn = wait_for_schema()
    cursor = conn.cursor()
    try:
        cursor.execute("SELECT COUNT(*) FROM admins WHERE username = %s", (DEMO_ADMIN,))
        if cursor.fetchone()[0]:
            print("Demo data already present, nothing to do.")
            return

        print("Seeding admins and platform settings...")
        insert_admins(cursor)
        cursor.execute("SELECT id FROM admins ORDER BY id LIMIT 1")
        insert_platform_settings(cursor, cursor.fetchone()[0])

        print("Seeding influencer applications and accounts...")
        insert_applications_and_influencers(cursor)
        cursor.execute("SELECT id FROM influencers")
        influencer_ids = [x[0] for x in cursor.fetchall()]

        print("Seeding clients, offers, orders and invoices...")
        client_ids = insert_clients(cursor)
        offer_ids = insert_offers(cursor, influencer_ids)
        insert_orders_and_invoices(cursor, offer_ids, client_ids, 200)

        promote_demo_accounts(cursor)
        conn.commit()
        print(f"Done. Log in as {DEMO_ADMIN}, {DEMO_CLIENT} or {DEMO_INFLUENCER} with password '{DEMO_PASSWORD}'.")
    except Exception:
        conn.rollback()
        raise
    finally:
        cursor.close()
        conn.close()


if __name__ == "__main__":
    main()
