describe("Client Account Management Flow", () => {
	const testUser = {
		username: "testuser",
		email: "test@example.com",
		password: "Test123!@#",
		instagramHandle: "testhandle",
		instagramToken: "IGQVJYeXample.Token123",
		billing: {
			firstName: "John",
			lastName: "Doe",
			streetAddress: "123 Test St",
			city: "Test City",
			state: "Test State",
			country: "United States",
			zipCode: "12345",
		},
		updatedEmail: "updated@example.com",
	};

	beforeEach(() => {
		cy.visit("/");
	});

	describe("Sign Up Process", () => {
		it("successfully completes the sign up form", () => {
			cy.contains("Sign Up").click();
			cy.url().should("include", "/sign-up");

			// Main details
			cy.get('input[name="username"]').type(testUser.username).wait(500);
			cy.get('input[name="email"]').type(testUser.email).wait(500);
			cy.get('input[name="password"]').type(testUser.password).wait(500);
			cy.get('input[name="instagramHandle"]')
				.type(testUser.instagramHandle)
				.wait(500);

			// Profile photo upload
			cy.fixture("test-profile.png", "base64")
				.then((fileContent) => {
					cy.get('input[type="file"]').attachFile({
						fileContent,
						fileName: "test-profile.png",
						mimeType: "image/png",
					});
				})
				.wait(1000);

			// Billing details
			cy.get('input[name="billingDetails.firstName"]')
				.type(testUser.billing.firstName)
				.wait(500);
			cy.get('input[name="billingDetails.lastName"]')
				.type(testUser.billing.lastName)
				.wait(500);
			cy.get('input[name="billingDetails.streetAddress"]')
				.type(testUser.billing.streetAddress)
				.wait(500);
			cy.get('input[name="billingDetails.city"]')
				.type(testUser.billing.city)
				.wait(500);
			cy.get('input[name="billingDetails.state"]')
				.type(testUser.billing.state)
				.wait(500);
			cy.get('select[name="billingDetails.country"]')
				.select(testUser.billing.country)
				.wait(500);
			cy.get('input[name="billingDetails.zipCode"]')
				.type(testUser.billing.zipCode)
				.wait(500);

			// Submit and verify
			cy.get('button[type="submit"]').click();
			cy.url().should("include", "/sign-in");
		});
	});

	describe("Sign In Process", () => {
		it("successfully signs in with created account", () => {
			cy.visit("/sign-in");
			cy.get('input[name="username"]').type(testUser.username).wait(500);
			cy.get('input[name="password"]').type(testUser.password).wait(500);
			cy.get('form > button[type="submit"]').click();

			// Verify login success
			cy.url().should("include", "/account");
			cy.window()
				.its("localStorage")
				.should("have.property", "likeme_access_token");
		});
	});

	describe("Instagram Connection", () => {
		it("successfully connects Instagram account", () => {
			cy.visit("/sign-in");
			cy.get('input[name="username"]').type(testUser.username);
			cy.get('input[name="password"]').type(testUser.password);
			cy.get('form > button[type="submit"]').click();
			cy.wait(1000);
			cy.visit("/account/settings/instagram");
			cy.get('input[name="instagram-access-token"]')
				.type(testUser.instagramToken)
				.wait(500);
			cy.contains("button", "Connect Instagram").click();

			// Verify connection
			cy.contains(`Connected to @${testUser.instagramHandle}`).should(
				"be.visible"
			);
			cy.wait(1000);
		});
	});

	describe("Profile Update", () => {
		it("successfully updates client information", () => {
			// Login
			cy.visit("/sign-in");
			cy.get('input[name="username"]').type(testUser.username).wait(500);
			cy.get('input[name="password"]').type(testUser.password).wait(500);
			cy.get('form > button[type="submit"]').click();
			cy.wait(1000);
			// Navigate to settings
			cy.visit("/account/settings");
			cy.wait(5000);

			// Click edit button
			cy.contains("button", "Edit Profile").click();
			cy.wait(500);

			// Update email
			cy.get('input[name="email"]')
				.clear()
				.type(testUser.updatedEmail)
				.wait(500);

			// Submit changes
			cy.contains("button", "Save Changes").click();
			cy.wait(1000);

			// Verify updated email is displayed
			cy.contains(testUser.updatedEmail).should("be.visible");

			// Refresh page to verify persistence
			cy.reload();
			cy.wait(1000);
			cy.contains(testUser.updatedEmail).should("be.visible");
		});
	});

	describe("Account Deletion", () => {
		it("successfully deletes account and prevents re-login", () => {
			// Login with updated email
			cy.visit("/sign-in");
			cy.get('input[name="username"]').type(testUser.username).wait(500);
			cy.get('input[name="password"]').type(testUser.password).wait(500);
			cy.get('form > button[type="submit"]').click();
			cy.wait(1000);

			// Navigate to settings
			cy.visit("/account/settings");
			cy.wait(1000);

			// Verify the updated email is still present
			cy.contains(testUser.updatedEmail).should("be.visible");

			// Delete account
			cy.contains("button", "Delete Account").click();
			cy.wait(500);
			cy.get(".bg-red-500").click(); // Confirm deletion
			cy.wait(1000);

			// Verify redirect to sign-in
			cy.url().should("include", "/sign-in");
			cy.wait(1000);

			// Attempt to login with deleted account
			cy.get('input[name="username"]').type(testUser.username).wait(500);
			cy.get('input[name="password"]').type(testUser.password).wait(500);
			cy.get('form > button[type="submit"]').click();

			// Verify login failure
			cy.url().should("include", "/sign-in");
		});
	});
});
