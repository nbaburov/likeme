import { AuthGuard } from "@/guards/AuthGuard";
import { AdminDashboard } from "@components/layout/admin/adminDashboard";

export default function AdminLayout({
	children,
}: {
	children: React.ReactNode;
}) {
	return (
		<AuthGuard allowedRoles={["ADMIN"]}>
			<AdminDashboard>
				<div className="p-6">{children}</div>
			</AdminDashboard>
		</AuthGuard>
	);
}
