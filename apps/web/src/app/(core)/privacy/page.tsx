import InfoPageLayout from "@components/layout/client/InfoPageLayout";
import { CustomSkeleton } from "@components/ui/CustomSkeleton";

export default function Privacy() {
	return (
		<InfoPageLayout title="Privacy Policy">
			<CustomSkeleton count={10} />
		</InfoPageLayout>
	);
}
