"use client";
import { AuthGuard } from "@/guards/AuthGuard";
import { ClientInfluencerDashboard } from "@/components/layout/influencer/ClientInfluencerDashboard";
import { useWebSocket } from "@/hooks/useWebSocket";

export default function InfluencerLayout({
	children,
}: Readonly<{
	children: React.ReactNode;
}>) {
	useWebSocket();

	return (
		<AuthGuard allowedRoles={["INFLUENCER"]}>
			<ClientInfluencerDashboard role="INFLUENCER">
				<div className="mt-6 p-6 bg-white/80 rounded-lg">
					{children}
				</div>
			</ClientInfluencerDashboard>
		</AuthGuard>
	);
}
