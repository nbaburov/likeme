/* eslint-disable react/no-unescaped-entities */
"use client";
import { motion, useInView } from "framer-motion";
import { useEffect, useRef, useState } from "react";
import { FiDollarSign, FiEye, FiPlay, FiSearch } from "react-icons/fi";

interface Feature {
	id: number;
	callout: string;
	title: string;
	description: string;
	contentPosition: "l" | "r";
	Icon: React.ElementType;
}

const features: Feature[] = [
	{
		id: 1,
		callout: "It's simple!",
		title: "Sign Up.",
		description:
			"Sign up as a client for free and start exploring our influencer marketplace.",
		contentPosition: "r",
		Icon: FiEye,
	},
	{
		id: 2,
		callout: "They're all here!",
		title: "Choose your Influencer.",
		description:
			"Browse through our influencer marketplace and find your favourite influencer for your needs.",
		contentPosition: "l",
		Icon: FiSearch,
	},
	{
		id: 3,
		callout: "Get Noticed!",
		title: "Choose your type of exposure.",
		description:
			"Choose from a variety of exposure types, including likes, comments, followers, and more.",
		contentPosition: "r",
		Icon: FiPlay,
	},
	{
		id: 4,
		callout: "Get Famous!",
		title: "Cha-ching!",
		description:
			"Get the exposure you want and pay for it with ease. Done in seconds.",
		contentPosition: "l",
		Icon: FiDollarSign,
	},
];

const Feautures = () => {
	const [featureInView, setFeatureInView] = useState<Feature>(features[0]);

	return (
		<section id="how-it-works" className="relative mx-auto max-w-7xl">
			<VanishText />
			<SlidingFeatureDisplay featureInView={featureInView} />

			<div className="-mt-[100vh] hidden md:block" />

			{features.map((s) => (
				<Content
					key={s.id}
					featureInView={s}
					setFeatureInView={setFeatureInView}
					{...s}
				/>
			))}
		</section>
	);
};

const SlidingFeatureDisplay = ({
	featureInView,
}: {
	featureInView: Feature;
}) => {
	return (
		<div
			style={{
				justifyContent:
					featureInView.contentPosition === "l"
						? "flex-end"
						: "flex-start",
			}}
			className="pointer-events-none sticky top-0 z-10 hidden h-screen w-full items-center justify-center md:flex"
		>
			<motion.div
				layout
				transition={{
					type: "spring",
					stiffness: 400,
					damping: 25,
				}}
				className="h-fit w-3/5 rounded-xl p-8"
			>
				<ExampleFeature featureInView={featureInView} />
			</motion.div>
		</div>
	);
};

const Content = ({
	setFeatureInView,
	featureInView,
}: {
	setFeatureInView: (feature: Feature) => void;
	featureInView: Feature;
}) => {
	const ref = useRef<HTMLDivElement | null>(null);
	const isInView = useInView(ref, {
		margin: "-150px",
	});

	useEffect(() => {
		if (isInView) {
			setFeatureInView(featureInView);
		}
	}, [isInView, featureInView, setFeatureInView]);

	return (
		<section
			ref={ref}
			className="relative z-0 flex h-fit md:h-screen"
			style={{
				justifyContent:
					featureInView.contentPosition === "l"
						? "flex-start"
						: "flex-end",
			}}
		>
			<div className="grid h-full w-full place-content-center px-4 py-12 md:w-2/5 md:px-8 md:py-8">
				<motion.div
					initial={{ opacity: 0, y: 25 }}
					whileInView={{ opacity: 1, y: 0 }}
					transition={{ duration: 0.5, ease: "easeInOut" }}
				>
					<span className="rounded-full bg-likeme-secondary px-2 py-1.5 text-sm font-semibold text-likeme-primary">
						{featureInView.callout}
					</span>
					<p className="my-3 text-5xl font-bold">
						{featureInView.title}
					</p>
					<p className="text-likeme-text">
						{featureInView.description}
					</p>
				</motion.div>
				<motion.div
					initial={{ opacity: 0, y: 25 }}
					whileInView={{ opacity: 1, y: 0 }}
					transition={{ duration: 0.5, ease: "easeInOut" }}
					className="mt-8 block md:hidden"
				>
					<ExampleFeature featureInView={featureInView} />
				</motion.div>
			</div>
		</section>
	);
};

const ExampleFeature = ({ featureInView }: { featureInView: Feature }) => {
	return (
		<div className="relative h-96 w-full rounded-full bg-likeme-secondary">
			<span className="absolute left-[50%] top-[50%] -translate-x-[50%] -translate-y-[50%] text-[10rem] text-likeme-accent">
				<featureInView.Icon />
			</span>
		</div>
	);
};

export const VanishText = () => {
	return (
		<div className="px-4 pt-12 text-center md:pt-24">
			<h3 className="text-3xl font-semibold text-red-200 sm:text-4xl md:text-5xl lg:text-6xl">
				Use LikeMe to
				<AnimatedText
					phrases={[
						"get exposure",
						"make a gift",
						"become famous",
						"impress everyone",
						"get likes",
						"show off",
						"be someone",
					]}
				/>
			</h3>
		</div>
	);
};

const ONE_SECOND = 1000;
const WAIT_TIME = ONE_SECOND * 3;

const AnimatedText = ({ phrases }: { phrases: string[] }) => {
	const [active, setActive] = useState(0);

	useEffect(() => {
		const intervalRef = setInterval(() => {
			setActive((pv) => (pv + 1) % phrases.length);
		}, WAIT_TIME);

		return () => clearInterval(intervalRef);
	}, [phrases]);

	return (
		<div className="relative mt-2 w-full capitalize">
			{phrases.map((phrase) => {
				const isActive = phrases[active] === phrase;
				return (
					<motion.div
						key={phrase}
						initial={false}
						animate={isActive ? "active" : "inactive"}
						style={{
							x: "-50%",
						}}
						variants={{
							active: {
								opacity: 1,
								scale: 1,
							},
							inactive: {
								opacity: 0,
								scale: 0,
							},
						}}
						className="absolute left-1/2 top-0 w-full font-bold text-likeme-primary "
					>
						{phrase}
					</motion.div>
				);
			})}
		</div>
	);
};

export default Feautures;
