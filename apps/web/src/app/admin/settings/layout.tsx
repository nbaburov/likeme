import { SettingsLayout } from "@/components/layout/SettingsLayout";

export default function AdminSettingsLayout({
	children,
}: Readonly<{
	children: React.ReactNode;
}>) {
	return <SettingsLayout>{children}</SettingsLayout>;
}
