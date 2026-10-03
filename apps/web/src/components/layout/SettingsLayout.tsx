"use client";
import { TabNavigation } from "@/components/layout/TabNavigation";
import { useRouter, usePathname } from "next/navigation";

const getSettingsTabs = (path: string) => {
	const isAdmin = path.includes("/admin");

	return isAdmin
		? [
				{ id: "", label: "Account" },
				{ id: "platform", label: "Platform Settings" },
		  ]
		: [
				{ id: "", label: "Account" },
				{ id: "payments", label: "Payments" },
				{ id: "instagram", label: "Instagram" },
		  ];
};

export const SettingsLayout = ({ children }: { children: React.ReactNode }) => {
	const router = useRouter();
	const pathname = usePathname();
	const baseUrl = pathname.split("/settings")[0];
	const currentTab = pathname.split("/settings/")[1] || "";
	const settingsTabs = getSettingsTabs(pathname);

	const handleTabChange = (tabId: string) => {
		const path = tabId
			? `${baseUrl}/settings/${tabId}`
			: `${baseUrl}/settings`;
		router.push(path);
	};

	return (
		<div className="container mx-auto px-4 py-8 space-y-6">
			<h1 className="text-2xl font-semibold">Settings</h1>
			<TabNavigation
				tabs={settingsTabs}
				activeTab={currentTab}
				onTabChange={handleTabChange}
			/>
			<div className="mt-6 p-6 bg-white/80 rounded-lg">{children}</div>
		</div>
	);
};
