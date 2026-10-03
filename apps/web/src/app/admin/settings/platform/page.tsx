"use client";
import { CustomSkeleton } from "@components/ui/CustomSkeleton";
import { usePlatformSettings } from "@hooks/usePlatformSettings";
import { SettingItem } from "@components/admin/settings/platform/SettingItem";
import { PlatformSettingType } from "@/dto/PlatformDTO";

export default function PlatformSettingsPage() {
	const {
		data: settings,
		isLoading,
		updateSetting,
		isUpdating,
	} = usePlatformSettings();

	if (isLoading) {
		return (
			<div className="space-y-6">
				<CustomSkeleton count={5} />
			</div>
		);
	}

	const handleUpdate = async (type: PlatformSettingType, value: string) => {
		await updateSetting({ type, value });
	};

	return (
		<div className="space-y-6">
			<div className="flex items-center justify-between">
				<h1 className="text-2xl font-bold">Platform Settings</h1>
			</div>
			<div className="space-y-4">
				{settings.map((setting) => (
					<SettingItem
						key={setting.id}
						setting={setting}
						onUpdate={handleUpdate}
						isUpdating={isUpdating}
					/>
				))}
			</div>
		</div>
	);
}
