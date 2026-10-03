import { toast } from "react-toastify";
import { ApiError } from "../dto/ErrorDTO";

export const useErrorToast = () => {
	const showError = (error: ApiError) => {
		const details = error.details as string;
		const detailsList = details.includes("\n\n")
			? details.split("\n\n")
			: [details];

		const content = (
			<div className="flex flex-col space-y-1">
				<span className="font-bold text-likeme-text">
					{error.title}
				</span>
				{typeof error.details === "string" &&
					(detailsList.length > 1 ? (
						<ul className="">
							{detailsList.map((detail, index) => (
								<li key={index} className="">
									<span className="text-sm font-thin text-gray-600">
										{detail}
									</span>
									{index < detailsList.length - 1 && (
										<hr className="mt-2 border-gray-200" />
									)}
								</li>
							))}
						</ul>
					) : (
						<span className="text-gray-600 text-sm font-thin">
							{details}
						</span>
					))}
			</div>
		);

		toast.error(content, {
			autoClose: false,
			className: "bg-red-50",
			style: { maxWidth: "400px" },
		});
	};

	return { showError };
};
