workspace "LikeMe Platform" "A platform connecting influencers with clients to manage Instagram services" {

    !identifiers hierarchical

    model {

        // Define people
        admin = person "Admin" "Manages the platform and oversees operations"
        client = person "Client" "Orders likes, comments, and follows on Instagram"
        influencer = person "Influencer" "Manages services and completes client orders"

        // Define external systems with gray backgrounds
        # stripe = softwareSystem "Stripe API" "External: Handles payments and payouts" {
        #     tags "External"
        # }
        # instagram = softwareSystem "Instagram API" "External: Handles authentication and social interactions" {
        #     tags "External"
        # }
        email = softwareSystem "SendGrid" "External: Handles email actions" {
            tags "External"
        }

        // Define LikeMe Platform as a software system
        likeMePlatform = softwareSystem "LikeMe Platform" "A platform for managing influencer services for clients" {

                // One Frontend container for all persons
                frontend = container "Frontend" "Web Application" "Next.js + TypeScript" "Allows clients, influencers, and admins to interact with the platform"
                
                // Backend API as a container
                backendAPI = container "Backend API" "Java Spring Boot" "Processes all business logic and API requests" {
                
                // Grouped Controllers
                group "Controllers" {
                    authController = component "AuthController" "Handles authentication and authorization" "Java Spring Boot"
                    clientController = component "ClientController" "Handles client CRUD operations" "Java Spring Boot"
                    adminController = component "AdminController" "Handles admin CRUD operations" "Java Spring Boot"
                    fileController = component "FileController" "Manages file uploads and downloads" "Java Spring Boot"
                    invoiceController = component "InvoiceController" "Handles invoice management and payments" "Java Spring Boot"
                    influencerController = component "InfluencerController" "Handles influencer and application management" "Java Spring Boot"
                    offerController = component "OfferController" "Handles offer CRUD and management" "Java Spring Boot"
                    platformSettingsController = component "PlatformSettingsController" "Manages platform settings" "Java Spring Boot"
                    orderController = component "OrderController" "Handles order management and completion" "Java Spring Boot"
                    analyticsController = component "AnalyticsController" "Handles analytics display" "Java Spring Boot"
                }

                // Grouped Services
                group "Services" {
                    // Authentication & Security
                    authService = component "AuthService" "Handles authentication and token management" "Java Spring Boot"
                    jwtService = component "JwtService" "Manages JWT token operations" "Java Spring Boot"
                    passwordService = component "PasswordService" "Handles password hashing and verification" "Java Spring Boot"
                    setupTokenService = component "SetupTokenService" "Manages influencer setup tokens" "Java Spring Boot"
                    
                    // User Management
                    adminService = component "AdminService" "Handles admin business logic" "Java Spring Boot"
                    clientService = component "ClientService" "Handles client business logic" "Java Spring Boot"
                    influencerService = component "InfluencerService" "Handles influencer business logic" "Java Spring Boot"
                    influencerApplicationService = component "InfluencerApplicationService" "Manages influencer applications" "Java Spring Boot"
                    
                    // Core Business
                    orderService = component "OrderService" "Handles order processing and management" "Java Spring Boot"
                    offerService = component "OfferService" "Manages offer lifecycle" "Java Spring Boot"
                    invoiceService = component "InvoiceService" "Handles invoice operations" "Java Spring Boot"
                    
                    // Platform
                    platformSettingsService = component "PlatformSettingsService" "Manages platform configuration" "Java Spring Boot"
                    analyticsService = component "AnalyticsService" "Manages platform configuration" "Java Spring Boot"
                    // Utils
                    emailService = component "EmailService" "Handles email notifications" "Java Spring Boot"
                    fileStorageService = component "FileStorageService" "Manages analytics operations" "Java Spring Boot"
                }
            
                group "Repositories" {
                    // User Management
                    adminRepository = component "AdminRepository" "Manages admin data persistence" "MySQL Repository"
                    clientRepository = component "ClientRepository" "Manages client data persistence" "MySQL Repository"
                    influencerRepository = component "InfluencerRepository" "Manages influencer data persistence" "MySQL Repository"
                    influencerApplicationRepository = component "InfluencerApplicationRepository" "Manages application data persistence" "MySQL Repository"
                    
                    // Core Business
                    orderRepository = component "OrderRepository" "Manages order data persistence" "MySQL Repository"
                    offerRepository = component "OfferRepository" "Manages offer data persistence" "MySQL Repository"
                    invoiceRepository = component "InvoiceRepository" "Manages invoice data persistence" "MySQL Repository"
                    
                    // Platform & Security
                    setupTokenRepository = component "SetupTokenRepository" "Manages setup token persistence" "MySQL Repository"
                    platformSettingsRepository = component "PlatformSettingsRepository" "Manages platform settings persistence" "MySQL Repository"
                }
            }

            // Database as a container
            database = container "MySQL Database" "Stores all platform data (orders, profiles, payments)" "MySQL"
        }

        // Relationships (System Context)
        admin -> likeMePlatform "Administers platform"
        # admin -> stripe "Manages payouts"
        
        client -> likeMePlatform "Uses platform"
        # client -> stripe "Makes payments"
        # client -> instagram "Authenticates"
        
        influencer -> likeMePlatform "Provides services"
        # influencer -> stripe "Receives payouts"
        # influencer -> instagram "Completes orders"
        
        # likeMePlatform -> stripe "Processes payments"
        # likeMePlatform -> instagram "Handles interactions"
        likeMePlatform -> email "Sends notifications"
        
        // Relationships (Container)
        likeMePlatform.frontend -> likeMePlatform.backendAPI "Makes API calls"
        
        # likeMePlatform.backendAPI -> stripe "Processes payments"
        # likeMePlatform.backendAPI -> instagram "Handles interactions"
        likeMePlatform.backendAPI -> email "Sends emails"
        likeMePlatform.backendAPI -> likeMePlatform.database "Uses for persistance"

        // Controller to Service connections
        likeMePlatform.backendAPI.authController -> likeMePlatform.backendAPI.authService "Uses"
        likeMePlatform.backendAPI.authController -> likeMePlatform.backendAPI.jwtService "Uses"
        
        likeMePlatform.backendAPI.analyticsController -> likeMePlatform.backendAPI.analyticsService "Uses"
        likeMePlatform.backendAPI.analyticsService -> likeMePlatform.backendAPI.orderRepository "Uses"
        

        likeMePlatform.backendAPI.clientController -> likeMePlatform.backendAPI.clientService "Uses"
        likeMePlatform.backendAPI.clientController -> likeMePlatform.backendAPI.passwordService "Uses"

        likeMePlatform.backendAPI.adminController -> likeMePlatform.backendAPI.adminService "Uses"
        likeMePlatform.backendAPI.adminController -> likeMePlatform.backendAPI.passwordService "Uses"

        likeMePlatform.backendAPI.fileController -> likeMePlatform.backendAPI.fileStorageService "Uses"

        likeMePlatform.backendAPI.invoiceController -> likeMePlatform.backendAPI.invoiceService "Uses"
        likeMePlatform.backendAPI.invoiceController -> likeMePlatform.backendAPI.orderService "Uses"
        likeMePlatform.backendAPI.invoiceController -> likeMePlatform.backendAPI.clientService "Uses"
        likeMePlatform.backendAPI.invoiceController -> likeMePlatform.backendAPI.jwtService "Uses"

        likeMePlatform.backendAPI.influencerController -> likeMePlatform.backendAPI.influencerService "Uses"
        likeMePlatform.backendAPI.influencerController -> likeMePlatform.backendAPI.influencerApplicationService "Uses"
        likeMePlatform.backendAPI.influencerController -> likeMePlatform.backendAPI.setupTokenService "Uses"

        likeMePlatform.backendAPI.offerController -> likeMePlatform.backendAPI.offerService "Uses"
        likeMePlatform.backendAPI.offerController -> likeMePlatform.backendAPI.influencerService "Uses"
        likeMePlatform.backendAPI.offerController -> likeMePlatform.backendAPI.jwtService "Uses"

        likeMePlatform.backendAPI.platformSettingsController -> likeMePlatform.backendAPI.platformSettingsService "Uses"

        likeMePlatform.backendAPI.orderController -> likeMePlatform.backendAPI.orderService "Uses"
        likeMePlatform.backendAPI.orderController -> likeMePlatform.backendAPI.clientService "Uses"
        likeMePlatform.backendAPI.orderController -> likeMePlatform.backendAPI.influencerService "Uses"
        likeMePlatform.backendAPI.orderController -> likeMePlatform.backendAPI.jwtService "Uses"

        // Service to Repository connections
        likeMePlatform.backendAPI.adminService -> likeMePlatform.backendAPI.adminRepository "Uses"
        likeMePlatform.backendAPI.clientService -> likeMePlatform.backendAPI.clientRepository "Uses"
        likeMePlatform.backendAPI.influencerService -> likeMePlatform.backendAPI.influencerRepository "Uses"
        likeMePlatform.backendAPI.influencerApplicationService -> likeMePlatform.backendAPI.influencerApplicationRepository "Uses"
        
        likeMePlatform.backendAPI.orderService -> likeMePlatform.backendAPI.orderRepository "Uses"
        likeMePlatform.backendAPI.orderService -> likeMePlatform.backendAPI.offerRepository "Uses"
        likeMePlatform.backendAPI.orderService -> likeMePlatform.backendAPI.invoiceRepository "Uses"
        
        likeMePlatform.backendAPI.offerService -> likeMePlatform.backendAPI.offerRepository "Uses"
        likeMePlatform.backendAPI.invoiceService -> likeMePlatform.backendAPI.invoiceRepository "Uses"
        
        likeMePlatform.backendAPI.setupTokenService -> likeMePlatform.backendAPI.setupTokenRepository "Uses"
        likeMePlatform.backendAPI.platformSettingsService -> likeMePlatform.backendAPI.platformSettingsRepository "Uses"
        
        // Cross-service dependencies
        likeMePlatform.backendAPI.influencerApplicationService -> likeMePlatform.backendAPI.emailService "Uses"
        likeMePlatform.backendAPI.influencerApplicationService -> likeMePlatform.backendAPI.setupTokenService "Uses"
        likeMePlatform.backendAPI.influencerService -> likeMePlatform.backendAPI.passwordService "Uses"
        likeMePlatform.backendAPI.orderService -> likeMePlatform.backendAPI.invoiceService "Uses"

        // Auth service dependencies
        likeMePlatform.backendAPI.authService -> likeMePlatform.backendAPI.adminRepository "Uses"
        likeMePlatform.backendAPI.authService -> likeMePlatform.backendAPI.clientRepository "Uses"
        likeMePlatform.backendAPI.authService -> likeMePlatform.backendAPI.influencerRepository "Uses"
        
        // External service connections
        likeMePlatform.backendAPI.emailService -> email "Sends emails via SendGrid"
    }

    views {

        // C1 - System Context Diagram with manual layout
        systemContext likeMePlatform "C1-System-Context" {
            include *
            autolayout lr
        }

        // C2 - Container Diagram
        container likeMePlatform "C2-Container-Diagram" {
            include *
            # include stripe
            # include instagram
            # exclude "influencer -> stripe"
            # exclude "influencer -> instagram"
            # exclude "client -> instagram"
            # exclude "client -> stripe"
            # exclude "admin -> stripe"
        }

        // C3 - Component Diagram for Backend API
        component likeMePlatform.backendAPI "C3-Backend-Component-Diagram" {
            include *
            # include instagram
            # include stripe
            autolayout lr
            
        }
        
        styles {
            element "External" {
                background #888888
                color #FFFFFF
            }
            element "Group:Controllers" {
                background #D6EAF8
            }
            element "Group:Services" {
                background #E6B0AA
            }
            element "Group:Repositories" {
                background #A9DFBF
            }
        }

        theme default
    }
}
