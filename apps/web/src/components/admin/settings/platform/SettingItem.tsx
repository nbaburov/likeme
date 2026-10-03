import {
	PlatformSettingsResponse,
	PlatformSettingType,
} from "@/dto/PlatformDTO";
import { useState } from "react";
import { Input } from "@components/ui/Input";
import { Button } from "@material-tailwind/react";
import { CustomSkeleton } from "@components/ui/CustomSkeleton";

interface SettingItemProps {
	setting: PlatformSettingsResponse;
	onUpdate: (type: PlatformSettingType, value: string) => void;
	isUpdating: boolean;
}

export function SettingItem({
	setting,
	onUpdate,
	isUpdating,
}: SettingItemProps) {
	const [value, setValue] = useState(setting.value);
	const [isEditing, setIsEditing] = useState(false);

	const handleSave = () => {
		onUpdate(setting.type, value);
		setIsEditing(false);
	};

	const handleCancel = () => {
		setValue(setting.value); // Reset to original value
		setIsEditing(false);
	};

	return (
		<div className="flex items-center justify-between p-4 border rounded-lg">
			<div className="flex-1">
				<h3 className="font-medium">
					{setting.type.replace(/_/g, " ")}
				</h3>
				<p className="text-sm text-gray-500">
					Last updated:{" "}
					{new Date(setting.updatedOn).toLocaleDateString()}
				</p>
			</div>
			<div className="flex items-center gap-2">
				{isEditing ? (
					<>
						<Input
							value={value}
							onChange={(e) => setValue(e.target.value)}
							className="w-32"
							disabled={isUpdating}
							label={setting.type.replace(/_/g, " ")}
							name={setting.type}
						/>
						<Button onClick={handleSave} disabled={isUpdating}>
							{isUpdating ? <CustomSkeleton count={1} /> : null}
							Save
						</Button>
						<Button
							variant="outline"
							onClick={handleCancel}
							disabled={isUpdating}
						>
							Cancel
						</Button>
					</>
				) : (
					<>
						<span className="px-3 py-1 bg-gray-100 rounded">
							{setting.value}
						</span>
						<Button
							variant="outline"
							onClick={() => setIsEditing(true)}
							disabled={isUpdating}
						>
							Edit
						</Button>
					</>
				)}
			</div>
		</div>
	);
}
