import Skeleton from "react-loading-skeleton";
import "react-loading-skeleton/dist/skeleton.css";

interface CustomSkeletonProps {
	count?: number;
	className?: string;
	circle?: boolean;
	baseColor?: string;
	highlightColor?: string;
}

export const CustomSkeleton = ({
	count = 1,
	className,
	circle = false,
	baseColor = "#ebebeb",
	highlightColor = "#f5f5f5"
}: CustomSkeletonProps) => {
	return (
		<Skeleton
			count={count}
			className={className}
			circle={circle}
			baseColor={baseColor}
			highlightColor={highlightColor}
		/>
	);
};
