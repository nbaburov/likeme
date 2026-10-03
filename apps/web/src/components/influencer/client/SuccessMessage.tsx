import { CheckCircleIcon } from "@heroicons/react/24/solid";
import { InfluencerApplicationResponse } from "@/dto/InfluencerApplicationDTO";

interface SuccessMessageProps {
	application: InfluencerApplicationResponse;
}

export default function SuccessMessage({ application }: SuccessMessageProps) {
	return (
		<div className="flex min-h-full flex-col items-center justify-center p-24">
			<div className="text-center">
				<CheckCircleIcon className="mx-auto h-16 w-16 text-green-500" />
				<h1 className="mt-4 text-3xl font-bold text-likeme-text">
					Application Submitted Successfully!
				</h1>
				<p className="mt-2 text-lg text-gray-600">
					Thank you for applying to be an influencer.
				</p>
				<p className="text-gray-600">
					We&apos;ll review your application and get back to you soon.
				</p>
				<div className="mt-8 text-left">
					<h2 className="text-xl font-semibold text-likeme-text">
						Application Details
					</h2>
					<div className="mt-4 rounded-lg bg-gray-50 p-6">
						<p className="text-sm text-gray-600">
							Reference ID:{" "}
							<span className="font-medium">
								{application.id}
							</span>
						</p>
						<p className="mt-2 text-sm text-gray-600">
							Submitted:{" "}
							<span className="font-medium">
								{new Date(
									application.createdOn
								).toLocaleDateString()}
							</span>
						</p>
					</div>
				</div>
			</div>
		</div>
	);
}
