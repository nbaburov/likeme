import { SettingsLayout } from "@/components/layout/SettingsLayout";

export default function ClientSettingsLayout({
	children,
}: Readonly<{
	children: React.ReactNode;
}>) {
	return <SettingsLayout>{children}</SettingsLayout>;
}
