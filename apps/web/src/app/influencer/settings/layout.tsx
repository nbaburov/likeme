import { SettingsLayout } from "@/components/layout/SettingsLayout";

export default function InfluencerSettingsLayout({
	children,
}: {
	children: React.ReactNode;
}) {
	return <SettingsLayout>{children}</SettingsLayout>;
}
