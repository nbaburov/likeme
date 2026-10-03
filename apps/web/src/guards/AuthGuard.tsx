"use client";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { useAuth, UserRole } from "@/hooks/useAuth";
import { Loader } from "@components/ui/BarLoader";
import { toast } from "react-toastify";

interface AuthGuardProps {
	children: React.ReactNode;
	allowedRoles?: UserRole[];
}

export const AuthGuard: React.FC<AuthGuardProps> = ({
	children,
	allowedRoles,
}) => {
	const { isAuthenticated, getUserRole } = useAuth();
	const router = useRouter();
	const [isChecking, setIsChecking] = useState(true);

	useEffect(() => {
		const checkAuth = () => {
			if (!isAuthenticated()) {
				toast.error("Please sign in to continue");
				router.push("/sign-in");
				return false;
			}

			if (allowedRoles) {
				const userRole = getUserRole();
				if (!userRole || !allowedRoles.includes(userRole)) {
					toast.error(
						"You don't have permission to access this page"
					);
					router.push("/");
					return false;
				}
			}
			return true;
		};

		const isAuthorized = checkAuth();
		setIsChecking(!isAuthorized);
	}, [isAuthenticated, getUserRole, router, allowedRoles]);

	if (isChecking) {
		return <Loader />;
	}

	return <>{children}</>;
};
